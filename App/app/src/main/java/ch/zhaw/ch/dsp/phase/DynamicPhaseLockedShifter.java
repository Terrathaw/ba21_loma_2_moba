package ch.zhaw.ch.dsp.phase;

import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import ch.zhaw.ch.dsp.ComplexFrame;
import ch.zhaw.ch.util.ArrayUtil;
import ch.zhaw.ch.util.DSPUtil;

public class DynamicPhaseLockedShifter extends PhaseReseter implements PhaseShifter {

    private static final String TAG = DynamicPhaseLockedShifter.class.getSimpleName();

    private float[] omegaConsts;
    private float[] phaseLast;
    private float[] phaseDelta;
    private float[] phaseDeltaLast;
    private float stretchFactor;
    private float magnitudeTolerance;
    private float maxMagnitude;
    private float[] magnitudeLast;


    @Override
    public void init(int frameSize, int frameSizeNyquist, int hopSizeAnalysis, float stretchFactor, int sampleRate, TransientDetectionType transientDetectionType, PhaseResetType phaseResetType) {
        this.frameSizeNyquist = frameSizeNyquist;
        this.stretchFactor = stretchFactor;
        float[] normalizedRange = ArrayUtil.div(ArrayUtil.range(frameSizeNyquist), frameSize);
        omegaConsts = ArrayUtil.mul(normalizedRange, (2f * DSPUtil.PI * hopSizeAnalysis));

        phaseLast = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
        phaseDelta = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
        phaseDeltaLast = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
        phaseTransformed = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);
        magnitudeLast = ArrayUtil.rangeOfValue(frameSizeNyquist, 0f);

        magnitudeTolerance = 10e-6f;

        maxMagnitude = 0;
        frameIndex = 0;
        setup(frameSize, frameSizeNyquist, sampleRate, transientDetectionType);
        setPhaseResetType(phaseResetType);
        this.transientDetection = transientDetection;
    }

    @Override
    public float[] shift(ComplexFrame frame) {
        float[] phaseCurrent = frame.getPhase();
        float[] magnitude = frame.getMagnitude();
        MidRange midRange = defaultMidRange;

        phaseDelta = ArrayUtil.sub(phaseCurrent, phaseLast);
        phaseDelta = ArrayUtil.sub(phaseDelta, omegaConsts);
        DSPUtil.princarg(phaseDelta);
        ArrayUtil.add2(phaseDelta, omegaConsts);
        ArrayUtil.mul2(phaseDelta, stretchFactor);

        boolean transientDetected = transientDetection.getTransientMode() != TransientDetectionType.NONE && transientDetection.hasTransient(magnitude);
        if(transientDetected) {
            Log.v("aaa", "transient detected");
            midRange = resetPhase(phaseCurrent);
        }

        if (!transientDetected || phaseResetType == PhaseResetType.BAND_LIMITED) {
            maxMagnitude = Float.max(ArrayUtil.max(magnitude), maxMagnitude);

            //key = bin position, value = magnitude
            Map<Integer, Float> magnitudeCurrentPeaks = getMagnitudePeaks(magnitude, (magnitudeTolerance * maxMagnitude), midRange);

            PriorityQueue<HeapBin> magnitudeHeap = new PriorityQueue<>(1, new HeapBinComparator());
            for (Map.Entry<Integer, Float> entry : magnitudeCurrentPeaks.entrySet()) {
                magnitudeHeap.add(new HeapBin(entry.getKey(), -1, magnitudeLast[entry.getKey()], 0));
            }

            for (int i = midRange.min; i < midRange.max; i++) {
                if (!magnitudeCurrentPeaks.containsKey(i))
                    phaseTransformed[i] = DSPUtil.princarg(phaseTransformed[i] + phaseDelta[i]);
            }

            int binIndex;
            int timeIndex;
            while (!magnitudeCurrentPeaks.isEmpty() && !magnitudeHeap.isEmpty()) {
                HeapBin maxBin = magnitudeHeap.poll();
                binIndex = maxBin.binIndex;
                timeIndex = maxBin.timeIndex;
                if (timeIndex < 0 && magnitudeCurrentPeaks.containsKey(binIndex)) {
                    phaseTransformed[binIndex] += ((phaseDeltaLast[binIndex] + phaseDelta[binIndex]) / 2);
                    magnitudeHeap.add(new HeapBin(binIndex, 0, magnitudeCurrentPeaks.get(binIndex),  DSPUtil.princarg(phaseTransformed[binIndex]- phaseCurrent[binIndex])));
                    magnitudeCurrentPeaks.remove(binIndex);
                } else if (timeIndex >= 0) {
                    for(int otherBinIndex = binIndex-1; otherBinIndex <= binIndex+1; otherBinIndex++){
                        if(magnitudeCurrentPeaks.containsKey(otherBinIndex)){
                            phaseTransformed[otherBinIndex] = DSPUtil.princarg(phaseCurrent[otherBinIndex] + maxBin.phaseRotation);
                            magnitudeHeap.add(new HeapBin(otherBinIndex, 0, magnitude[otherBinIndex], maxBin.phaseRotation));
                            magnitudeCurrentPeaks.remove(otherBinIndex);
                        }
                    }
                }
            }
        }

        phaseLast = Arrays.copyOf(phaseCurrent, phaseCurrent.length);
        phaseDeltaLast = Arrays.copyOf(phaseDelta, phaseCurrent.length);
        magnitudeLast = Arrays.copyOf(magnitude, magnitude.length);
        frameIndex++;

        return phaseTransformed;
    }

    private Map<Integer, Float> getMagnitudePeaks(float[] magnitude, float minMagnitude, MidRange midRange) {
        Map<Integer, Float> magnitudeCurrentPeaks = new HashMap();

        for (int i = midRange.min; i < midRange.max; i++) {
            if (magnitude[i] > minMagnitude) {
                magnitudeCurrentPeaks.put(i, magnitude[i]);
            }
        }

        return magnitudeCurrentPeaks;
    }

    class HeapBin {
        int binIndex;
        int timeIndex;
        float magnitude;
        float phaseRotation;

        public HeapBin(int binIndex, int timeIndex, float magnitude, float phaseRotation) {
            this.binIndex = binIndex;
            this.timeIndex = timeIndex;
            this.magnitude = magnitude;
            this.phaseRotation = phaseRotation;
        }
    }

    class HeapBinComparator implements Comparator<HeapBin> {

        @Override
        public int compare(HeapBin o1, HeapBin o2) {
            if (o1.magnitude < o2.magnitude) {
                return 1;
            } else if (o1.magnitude > o2.magnitude) {
                return -1;
            }
            return 0;
        }
    }

}
