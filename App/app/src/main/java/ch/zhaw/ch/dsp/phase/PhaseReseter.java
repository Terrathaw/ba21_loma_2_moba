package ch.zhaw.ch.dsp.phase;

import android.util.Log;

public abstract class PhaseReseter {
    protected PhaseResetType phaseResetType = PhaseResetType.BAND_LIMITED;
    protected float[] phaseTransformed;
    protected int frameSizeNyquist;
    protected int frameIndex;
    protected int bandLow;
    protected int bandHigh;
    protected MidRange defaultMidRange;
    protected TransientDetection transientDetection;


    protected void setup(int frameSize, int frameSizeNyquist, int sampleRate, TransientDetectionType transientDetectionType){
        bandLow = (int) Math.floor(150*frameSize / sampleRate);
        bandHigh = (int) Math.floor(1000*frameSize / sampleRate);
        Log.v("aaa", ""+frameSizeNyquist);
        defaultMidRange = new MidRange(0, frameSizeNyquist);
        transientDetection = new TransientDetection(frameSizeNyquist, transientDetectionType);
    }

    protected MidRange resetPhase(float[] phase){

        if(phaseResetType.equals(PhaseResetType.BAND_LIMITED)){
            for(int i = 0; i < bandLow; i++){
                phaseTransformed[i] = phase[i];
            }
            for(int i = bandHigh; i < frameSizeNyquist; i++){
                phaseTransformed[i] = phase[i];
            }
            return new MidRange(bandLow, bandHigh);
        }else{
            phaseTransformed = phase;
            return defaultMidRange;
        }
    }

    public void setPhaseResetType(PhaseResetType phaseResetType) {
        Log.v("aaa", phaseResetType.getClassName());
        this.phaseResetType = phaseResetType;
    }

    class MidRange{
        public int min;
        public int max;
        MidRange(int min, int max){
            this.min = min;
            this.max = max;
        }
    }
}