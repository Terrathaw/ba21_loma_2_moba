package ch.zhaw.ch.dsp.phase;

import android.util.Log;

import java.util.Arrays;

import ch.zhaw.ch.dsp.ComplexFrame;
import ch.zhaw.ch.util.ArrayUtil;
import ch.zhaw.ch.util.DSPUtil;

public class BasicPhaseShifter extends PhaseReseter implements PhaseShifter {

    private float[] omegaConsts;
    private float[] lastPhase;
    private float[] phaseDelta;
    private float stretchFactor;
    private float[] phaseTransformedTemp;


    @Override
    public void init(int frameSize, int frameSizeNyquist, int hopSizeAnalysis, float stretchFactor, int sampleRate, TransientDetectionType transientDetectionType, PhaseResetType phaseResetType) {
        this.stretchFactor = stretchFactor;
        this.frameSizeNyquist = frameSizeNyquist;
        setup(frameSize, frameSizeNyquist, sampleRate, transientDetectionType);
        setPhaseResetType(phaseResetType);
        float[] normalizedRange = ArrayUtil.div(ArrayUtil.range(frameSizeNyquist), frameSize);
        omegaConsts = ArrayUtil.mul(normalizedRange, (2f * DSPUtil.PI * hopSizeAnalysis));

        lastPhase = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
        phaseDelta = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
        phaseTransformed = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
    }

    @Override
    public float[] shift(ComplexFrame frame) {
        float[] currentPhase = frame.getPhase();
        phaseDelta = ArrayUtil.sub(currentPhase, lastPhase);
        lastPhase = currentPhase;
        MidRange midRange = defaultMidRange;

        boolean transientDetected = transientDetection.getTransientMode() != TransientDetectionType.NONE && transientDetection.hasTransient(frame.getMagnitude());

        if(transientDetected) {
            Log.v("aaa", "transient detected");
            midRange = resetPhase(currentPhase);
        }

        if (!transientDetected || phaseResetType == PhaseResetType.BAND_LIMITED) {
            if(phaseResetType == PhaseResetType.BAND_LIMITED) {
                phaseTransformedTemp = calculate(
                        Arrays.copyOfRange(phaseTransformed, midRange.min, midRange.max),
                        Arrays.copyOfRange(phaseDelta, midRange.min, midRange.max),
                        Arrays.copyOfRange(omegaConsts, midRange.min, midRange.max)
                );
            }else {
                phaseTransformedTemp = calculate(phaseTransformed, phaseDelta, omegaConsts);
            }
            for(int i = 0; i < phaseTransformedTemp.length; i++){
                phaseTransformed[midRange.min+i] = phaseTransformedTemp[i];
            }
        }

        return phaseTransformed;
    }

    public float[] calculate(float[] phaseTransformed, float[] phaseDelta, float[] omegaConsts){
        ArrayUtil.sub2(phaseDelta, omegaConsts);
        DSPUtil.princarg(phaseDelta);
        ArrayUtil.add2(phaseDelta, omegaConsts);
        ArrayUtil.mul2(phaseDelta, stretchFactor);
        ArrayUtil.add2(phaseTransformed, phaseDelta);
        DSPUtil.princarg(phaseTransformed);
        return phaseTransformed;
    }

}
