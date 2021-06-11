package ch.zhaw.ch.dsp.phase;

import android.util.Log;

import ch.zhaw.ch.util.ArrayUtil;
import ch.zhaw.ch.util.AverageQueue;
import ch.zhaw.ch.util.MovingMedian;

public class TransientDetection {
    private float[] lastMagnitude;
    private float lastHighFreq;
    private TransientDetectionType transientMode = TransientDetectionType.NONE;
    private MovingMedian highFreqFilter;
    private MovingMedian highFreqDerivFilter;
    private float lastHighFreqDerivDelta;
    private int risingCount;
    private float previousTransientLikelihood;
    private float transientLikelihoodThrehold = 0.35f;
    private float minMagnitudeMod = 10e-6f;
    private AverageQueue maxMagnitudeAvgQueue;
    //amplitude (root-power) ratio equivalent to 3 dB (10**(3/20)=10**0.15)
    //mag1 / mag2 = 10**0.15 ---> mag1 is 3 dB over mag2
    private float magnitudeRatio3db = (float) Math.pow(10, 0.15);

    public TransientDetection(int frameSizeNyquist, TransientDetectionType transientMode){
        Log.v("PitchShifter", transientMode.getClassName());
        this.transientMode = transientMode;
        highFreqFilter = new MovingMedian(19, 85);
        highFreqDerivFilter = new MovingMedian(19, 90);
        maxMagnitudeAvgQueue = new AverageQueue(19);
        lastMagnitude = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
    }

    public boolean hasTransient(float[] currentMagnitude){
        float transientLikelihood = getTransients(currentMagnitude);
        if(transientLikelihood > 0 &&  transientLikelihood > previousTransientLikelihood && transientLikelihood > transientLikelihoodThrehold){
            previousTransientLikelihood = transientLikelihood;
            return true;
        }
        previousTransientLikelihood = transientLikelihood;

        return false;
    }

    public TransientDetectionType getTransientMode(){
        return transientMode;
    }

    private float highFreqDetection(float[] currentMagnitude){
        float transientProbability = 0.0f;
        float highFreq  = 0.0f;
        float highFreqDeriv;
        float highFreqDerivDelta = 0f;
        float highFreqExcess;

        for (int i = 0; i < currentMagnitude.length; i++) {
            highFreq +=  currentMagnitude[i]*i;
        }
        highFreqDeriv = highFreq - lastHighFreq;

        highFreqFilter.Put(highFreq);
        highFreqDerivFilter.Put(highFreqDeriv);
        this.lastHighFreq = highFreq;

        highFreqExcess = highFreq - highFreqFilter.Get();
        if(highFreqExcess > 0)
            highFreqDerivDelta = highFreqDeriv - highFreqDerivFilter.Get();

        if(highFreqDerivDelta < this.lastHighFreqDerivDelta){
            if(risingCount > 3 && lastHighFreqDerivDelta > 0)
                transientProbability = 0.5f;
            risingCount = 0;
        }else{
            risingCount++;
        }
        this.lastHighFreqDerivDelta = highFreqDerivDelta;
        return transientProbability;
    }

    private float percussiveDetection(float[] currentMagnitude){
        maxMagnitudeAvgQueue.push(ArrayUtil.max(currentMagnitude));
        float zeroThresh = minMagnitudeMod * maxMagnitudeAvgQueue.getAvg();
        int count = 0;
        int nonZeroCount = 0;
        float magnitudeIncreaseRatio;

        for(int i = 0; i < currentMagnitude.length; i++){
            magnitudeIncreaseRatio = 0.0f;
            if(lastMagnitude[i] > zeroThresh)
                magnitudeIncreaseRatio = currentMagnitude[i]/lastMagnitude[i];
            else if(currentMagnitude[i] > zeroThresh)
                magnitudeIncreaseRatio = magnitudeRatio3db;
            if(magnitudeIncreaseRatio >= magnitudeRatio3db)
                count +=1;
            if(currentMagnitude[i] > zeroThresh)
                nonZeroCount +=1;
        }
        System.arraycopy(currentMagnitude, 0, lastMagnitude, 0, currentMagnitude.length);
        if(nonZeroCount == 0)
            return 0f;
        return count/nonZeroCount;
    }

    private float getTransients(float[] currentMagnitude) {
        if (transientMode == TransientDetectionType.PERCUSSIVE){
            return percussiveDetection(currentMagnitude);
        }else if(transientMode == TransientDetectionType.COMPOUND){
            return Math.max(percussiveDetection(currentMagnitude),highFreqDetection(currentMagnitude));
        }else if(transientMode == TransientDetectionType.HIGH_FREQ){
            return highFreqDetection(currentMagnitude);
        }
        return 0;
    }
}
