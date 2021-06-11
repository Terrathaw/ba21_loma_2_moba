package ch.zhaw.ch.dsp.phase;

import java.util.Arrays;

import ch.zhaw.ch.dsp.ComplexFrame;
import ch.zhaw.ch.util.ArrayUtil;
import ch.zhaw.ch.util.DSPUtil;

/***
 * Based on DAFX Chapter 7.3.5 Phase unwrapping http://dafx.de/DAFX_Book_Page/index.html
 */
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
        resetCurrentMidRange();

        boolean transientDetected = transientDetection.getTransientMode() != TransientDetectionType.NONE && transientDetection.detectTransients(frame.getMagnitude());

        if(transientDetected) {
            resetPhase(currentPhase);
        }

        if (!transientDetected || phaseResetType == PhaseResetType.BAND_LIMITED) {
            if(phaseResetType == PhaseResetType.BAND_LIMITED) {
                phaseTransformedTemp = calculate(
                        Arrays.copyOfRange(phaseTransformed, currentMidRange[0], currentMidRange[1]),
                        Arrays.copyOfRange(phaseDelta, currentMidRange[0], currentMidRange[1]),
                        Arrays.copyOfRange(omegaConsts, currentMidRange[0], currentMidRange[1])
                );
            }else {
                phaseTransformedTemp = calculate(phaseTransformed, phaseDelta, omegaConsts);
            }
            for(int i = 0; i < phaseTransformedTemp.length; i++){
                phaseTransformed[currentMidRange[0]+i] = phaseTransformedTemp[i];
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
