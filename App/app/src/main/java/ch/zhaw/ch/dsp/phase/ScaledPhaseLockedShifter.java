package ch.zhaw.ch.dsp.phase;

import java.util.ArrayList;
import java.util.Arrays;

import ch.zhaw.ch.dsp.ComplexFrame;
import ch.zhaw.ch.util.ArrayUtil;
import ch.zhaw.ch.util.DSPUtil;

/***
 * Based on Improved Phase Vocoder Time-Scale Modification of Audio Chapter III.C.2 Scaled Phase Locking https://ieeexplore.ieee.org/document/759041
 * and DAFX Chapter 7.4.3 Phase locked vocoder http://dafx.de/DAFX_Book_Page/index.html
 */
public class ScaledPhaseLockedShifter extends PhaseReseter implements PhaseShifter {
    private final String TAG = ScaledPhaseLockedShifter.class.getSimpleName();

    private float[] expectedPhaseDelta;
    private float[] lastPhase;
    private ArrayList<Integer> lastMagnitudePeaks;
    private int shadowPeak = 1;
    private int frameIndex = 0;
    private int frameSize;
    private int hopSizeAnalysis;
    private float stretchFactor;
    private int lowerBoundIndexPosition = 0;
    private int upperBoundIndexPosition = 1;
    private float scaleFactor = 1;

    @Override
    public void init(int frameSize, int frameSizeNyquist, int hopSizeAnalysis, float stretchFactor, int sampleRate, TransientDetectionType transientDetectionType, PhaseResetType phaseResetType) {
        this.frameSize = frameSize;
        this.frameSizeNyquist = frameSizeNyquist;
        this.hopSizeAnalysis = hopSizeAnalysis;
        this.stretchFactor = stretchFactor;

        expectedPhaseDelta = ArrayUtil.div(ArrayUtil.mul(ArrayUtil.range(frameSizeNyquist), (2f * DSPUtil.PI * hopSizeAnalysis)), (float) frameSize);
        lastPhase = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
        phaseTransformed = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
        lastMagnitudePeaks = new ArrayList<>();

        setup(frameSize, frameSizeNyquist, sampleRate, transientDetectionType);
        setPhaseResetType(phaseResetType);
    }

    @Override
    public float[] shift(ComplexFrame frame) {
        float[] currentPhase = frame.getPhase();
        float[] currentMagnitude = frame.getMagnitude();
        float[] phaseTransformedTemp;
        ArrayList<Integer> currentMagnitudePeaks = getMagnitudePeaks(currentMagnitude);
        resetCurrentMidRange();

        boolean transientDetected = transientDetection.getTransientMode() != TransientDetectionType.NONE && transientDetection.detectTransients(frame.getMagnitude());

        if (transientDetected)
            resetPhase(currentPhase);

        if (!transientDetected || phaseResetType == PhaseResetType.BAND_LIMITED) {
            if (currentMagnitudePeaks.size() > 0 && lastMagnitudePeaks.size() > 0) {
                phaseTransformedTemp = magnitudePeakTransformation(currentPhase, currentMagnitude, currentMagnitudePeaks, transientDetected);
                for (int i = currentMidRange[0]; i < currentMidRange[1]; i++) {
                    phaseTransformed[i] = phaseTransformedTemp[i];
                }
            } else {
                normalCalculation(currentPhase);
            }
        }

        lastPhase = Arrays.copyOf(currentPhase, currentPhase.length);
        lastMagnitudePeaks = new ArrayList<>(currentMagnitudePeaks);
        frameIndex++;

        return phaseTransformed;
    }

    private float[] magnitudePeakTransformation(float[] currentPhase, float[] currentMagnitude, ArrayList<Integer> currentMagnitudePeaks, boolean transientDetected) {
        float[] phaseTransformedTemp = new float[frameSizeNyquist];
        int previousPeakPosition = 0;
        int currentPeakIndex;
        int upperBoundIndex = 0;
        int lowerBoundIndex = 0;
        for (int currentPeakPosition = 0; currentPeakPosition < currentMagnitudePeaks.size(); currentPeakPosition++) {
            currentPeakIndex = currentMagnitudePeaks.get(currentPeakPosition);
            previousPeakPosition = getRelatedPeak(previousPeakPosition, currentPeakIndex);
            if (transientDetected && phaseResetType == PhaseResetType.BAND_LIMITED && (currentPeakIndex < bandLow || currentPeakIndex >= bandHigh)) continue;
            float[] values = getPhaseRotation(currentPhase, lastMagnitudePeaks.get(previousPeakPosition), currentPeakIndex);
            float peakSynthesisPhase = values[0];
            float peakPhaseUnwrapped = values[1];
            lowerBoundIndex = upperBoundIndex;
            upperBoundIndex = getUpperInfluenceBounds(currentMagnitudePeaks, currentMagnitude, currentPeakIndex);
            for (int i = lowerBoundIndex; i <= upperBoundIndex; i++) {
                float unwrappedPhase = this.lastPhase[i] + expectedPhaseDelta[i] + DSPUtil.princarg(currentPhase[i] - lastPhase[i] - expectedPhaseDelta[i]);
                phaseTransformedTemp[i] = peakSynthesisPhase + scaleFactor * (unwrappedPhase - peakPhaseUnwrapped);
            }
        }

        return phaseTransformedTemp;
    }

    private int getUpperInfluenceBounds(ArrayList<Integer> currentMagnitudePeaks, float[] magnitude, int currentPeakIndex) {
        if (currentMagnitudePeaks.size() <= 1 || currentPeakIndex == currentMagnitudePeaks.get(currentMagnitudePeaks.size() - 1)) {
            return frameSizeNyquist - 1;
        }
        int upperBoundIndex = currentPeakIndex;
        while (upperBoundIndex < this.frameSizeNyquist - 1 && magnitude[upperBoundIndex] > magnitude[upperBoundIndex + 1]) upperBoundIndex++;
        return upperBoundIndex;
    }

    private float[] getPhaseRotation(float[] currentPhase, int lastPeakIndex, int currentPeakIndex) {
        float averagePeakIndex = (currentPeakIndex + lastPeakIndex) / 2f;
        float expectedPeakPhaseDelta = 2f * DSPUtil.PI * hopSizeAnalysis * averagePeakIndex / frameSize;
        float peakPhaseDelta = expectedPeakPhaseDelta + DSPUtil.princarg(currentPhase[currentPeakIndex] - lastPhase[lastPeakIndex] - expectedPeakPhaseDelta);
        float peakPhaseUnwrapped = lastPhase[currentPeakIndex] + peakPhaseDelta;
        return new float[]{DSPUtil.princarg(phaseTransformed[currentPeakIndex] + peakPhaseDelta * stretchFactor), peakPhaseUnwrapped};
    }

    private int getRelatedPeak(int previousPeakPosition, int currentPeakIndex) {
        while (previousPeakPosition < (lastMagnitudePeaks.size() - 1) &&
                Math.abs(currentPeakIndex - lastMagnitudePeaks.get(previousPeakPosition + 1)) <
                        Math.abs(currentPeakIndex - lastMagnitudePeaks.get(previousPeakPosition))) {
            previousPeakPosition++;
        }
        return previousPeakPosition;
    }

    private void normalCalculation(float[] currentPhase) {
        float deltaPhase;
        for (int i = currentMidRange[0]; i < currentMidRange[1]; i++) {
            deltaPhase = expectedPhaseDelta[i] + DSPUtil.princarg(currentPhase[i] - lastPhase[i] - expectedPhaseDelta[i]);
            phaseTransformed[i] = phaseTransformed[i] + deltaPhase * stretchFactor;
        }
    }

    private ArrayList<Integer> getMagnitudePeaks(float[] magnitude) {
        ArrayList<Integer> currentMagnitudePeaks = new ArrayList<>();
        boolean isPeak;
        for (int i = shadowPeak; i < (frameSizeNyquist - shadowPeak); i++) {
            if (magnitude[i] < 1) continue;
            isPeak = true;
            for (int j = (-shadowPeak); j <= shadowPeak; j++) {
                if (magnitude[i] < magnitude[i + j]) {
                    isPeak = false;
                    break;
                }
            }
            if (isPeak) {
                currentMagnitudePeaks.add(i);
                i += shadowPeak;
            }
        }
        return currentMagnitudePeaks;
    }


}
