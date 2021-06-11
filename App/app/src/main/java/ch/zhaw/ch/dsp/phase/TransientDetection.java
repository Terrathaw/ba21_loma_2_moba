package ch.zhaw.ch.dsp.phase;

import android.util.Log;

import ch.zhaw.ch.util.ArrayUtil;
import ch.zhaw.ch.util.AverageQueue;
import ch.zhaw.ch.util.MovingMedian;

/***
 * Adds functionalities to detect transients in currently processed frames with their magnitude.
 *
 */

public class TransientDetection {
    private float[] lastMagnitude;
    private float lastHighFreq;
    private TransientDetectionType transientMode;
    private MovingMedian highFreqFilter;
    private MovingMedian highFreqDerivFilter;
    private float lastHighFreqDerivDelta;
    private int risingCount;
    private float previousTransientLikelihood;
    private float transientLikelihoodThrehold = 0.35f;
    private float minMagnitudeMod = 10e-6f;
    private AverageQueue maxMagnitudeAvgQueue;
    private float magnitudeRatio3db = (float) Math.pow(10, 0.15);

    /***
     *
     * @param frameSizeNyquist To create an internal buffer for the last magnitude
     * @param transientMode The transient detection type this class should use.
     */

    public TransientDetection(int frameSizeNyquist, TransientDetectionType transientMode){
        Log.v("PitchShifter", transientMode.getIdentifier());
        this.transientMode = transientMode;
        highFreqFilter = new MovingMedian(19, 85);
        highFreqDerivFilter = new MovingMedian(19, 90);
        maxMagnitudeAvgQueue = new AverageQueue(19);
        lastMagnitude = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
    }

    /***
     * Calculates if a transient is detected by comparing the current transient likelihood with the
     * last likelihood and a likelihood threshold.
     * @param magnitude
     * @return If transient was detected
     */
    public boolean detectTransients(float[] magnitude){
        float transientLikelihood = getTransients(magnitude);
        if(transientLikelihood > previousTransientLikelihood && transientLikelihood > transientLikelihoodThrehold){
            previousTransientLikelihood = transientLikelihood;
            return true;
        }
        previousTransientLikelihood = transientLikelihood;

        return false;
    }

    /***
     * Transient mode used by this class.
     * @return Transient mode
     */
    public TransientDetectionType getTransientMode(){
        return transientMode;
    }

    /***
     * Sums the magnitudes of all bins weighted by their frequencies and puts the results in a moving median.
     * If the median is rising three subsequent frames a transient is detected
     *
     * @param magnitude
     * @return High frequency transient probability in range 0 to 1
     */
    private float highFreqDetection(float[] magnitude){
        float transientProbability = 0.0f;
        float highFreq  = 0.0f;
        float highFreqDeriv;
        float highFreqDerivDelta = 0f;
        float highFreqExcess;

        for (int i = 0; i < magnitude.length; i++) {
            highFreq +=  magnitude[i]*i;
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

    /***
     * Calculates the percussive transient probability by counting all the significant and the non zero bins.
     *
     * @param magnitude
     * @return Percussive transient probability in range 0 to 1
     */
    private float percussiveDetection(float[] magnitude){
        maxMagnitudeAvgQueue.push(ArrayUtil.max(magnitude));
        float zeroThresh = minMagnitudeMod * maxMagnitudeAvgQueue.getAvg();
        int count = 0;
        int nonZeroCount = 0;
        float magnitudeIncreaseRatio;

        for(int i = 0; i < magnitude.length; i++){
            magnitudeIncreaseRatio = 0.0f;
            if(lastMagnitude[i] > zeroThresh)
                magnitudeIncreaseRatio = magnitude[i]/lastMagnitude[i];
            else if(magnitude[i] > zeroThresh)
                magnitudeIncreaseRatio = magnitudeRatio3db;
            if(magnitudeIncreaseRatio >= magnitudeRatio3db)
                count +=1;
            if(magnitude[i] > zeroThresh)
                nonZeroCount +=1;
        }
        System.arraycopy(magnitude, 0, lastMagnitude, 0, magnitude.length);
        if(nonZeroCount == 0)
            return 0f;
        return count/nonZeroCount;
    }

    /***
     * Calculates transient probability according to the selected TransientDetectionType.
     *
     * @param magnitude
     * @return Transient probability in range 0 to 1
     */
    private float getTransients(float[] magnitude) {
        if (transientMode == TransientDetectionType.PERCUSSIVE){
            return percussiveDetection(magnitude);
        }else if(transientMode == TransientDetectionType.COMPOUND){
            return Math.max(percussiveDetection(magnitude),highFreqDetection(magnitude));
        }else if(transientMode == TransientDetectionType.HIGH_FREQ){
            return highFreqDetection(magnitude);
        }
        return 0;
    }
}
