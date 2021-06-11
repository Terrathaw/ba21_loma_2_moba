package ch.zhaw.ch.dsp.resample;

import ch.zhaw.ch.util.ArrayUtil;

/***
 * Based on DAFX Chapter 7.4.4 Block by block approach http://dafx.de/DAFX_Book_Page/index.html
 */
public class LinearInterpolator implements Resampler {
    private int resampleFrameSize;
    private int[] resampleIndexesLeft;
    private int[] resampleIndexesRight;
    private float[] resampleIndexWeightsLeft;
    private float[] resampleIndexWeightsRight;
    private float[] frameExtended;

    @Override
    public void init(int frameSize, float stretchFactor) {
        resampleFrameSize = (int) Math.floor(frameSize / stretchFactor);
        float[] resamplePositions = ArrayUtil.mul(ArrayUtil.range(resampleFrameSize), stretchFactor);
        resampleIndexesLeft = ArrayUtil.floor(resamplePositions);
        resampleIndexesRight = ArrayUtil.add(resampleIndexesLeft, 1);
        resampleIndexWeightsRight = ArrayUtil.sub(resamplePositions, resampleIndexesLeft);
        resampleIndexWeightsLeft = ArrayUtil.sub(ArrayUtil.rangeOfValue(resampleFrameSize, 1), resampleIndexWeightsRight);
        int iMax = resampleFrameSize - 1;
        if (resampleIndexesRight[iMax] > frameSize - 1) {
            resampleIndexWeightsRight[iMax] = 0;
            resampleIndexWeightsLeft[iMax] = 1;
        }

        frameExtended = ArrayUtil.rangeOfValue(frameSize + 1, 0f);
    }

    @Override
    public int getResampleFrameSize() {
        return resampleFrameSize;
    }

    @Override
    public void resample(float[] frame, float[] resampledFrame) {
        System.arraycopy(frame, 0, frameExtended, 0, frame.length);
        for (int j = 0; j < resampleFrameSize; j++) {
            resampledFrame[j] = frameExtended[resampleIndexesLeft[j]] * resampleIndexWeightsLeft[j]
                    + frameExtended[resampleIndexesRight[j]] * resampleIndexWeightsRight[j];
        }
    }
}
