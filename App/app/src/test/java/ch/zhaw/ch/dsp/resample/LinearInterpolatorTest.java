package ch.zhaw.ch.dsp.resample;

import org.junit.Assert;
import org.junit.Test;

import ch.zhaw.ch.util.ArrayUtil;

public class LinearInterpolatorTest {

    @Test
    public void resample_isCorrect() {
        float tolerance = 0.0f;
        int frameSize = 100;
        float stretchFactor = 1f;
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        linearInterpolator.init(frameSize, stretchFactor);

        float[] frame = ArrayUtil.intToFloat(ArrayUtil.range(frameSize));
        float[] expectedResampledFrame = ArrayUtil.range(linearInterpolator.getResampleFrameSize(), 0, stretchFactor);
        float[] resampledFrame = new float[linearInterpolator.getResampleFrameSize()];

        linearInterpolator.resample(frame, resampledFrame);
        Assert.assertArrayEquals(expectedResampledFrame, resampledFrame, tolerance);
    }

    @Test
    public void resample_upsample_isCorrect() {
        float tolerance = 0.0f;
        int frameSize = 100;
        float stretchFactor = 0.5f;
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        linearInterpolator.init(frameSize, stretchFactor);

        float[] frame = ArrayUtil.intToFloat(ArrayUtil.range(frameSize));
        float[] expectedResampledFrame = ArrayUtil.range(linearInterpolator.getResampleFrameSize(), 0, stretchFactor);
        expectedResampledFrame[linearInterpolator.getResampleFrameSize() - 1] = frame[frameSize - 1];
        float[] resampledFrame = new float[linearInterpolator.getResampleFrameSize()];

        linearInterpolator.resample(frame, resampledFrame);
        Assert.assertArrayEquals(expectedResampledFrame, resampledFrame, tolerance);
    }

    @Test
    public void resample_downsample_isCorrect() {
        float tolerance = 0.0f;
        int frameSize = 100;
        float stretchFactor = 1.2f;
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        linearInterpolator.init(frameSize, stretchFactor);

        float[] frame = ArrayUtil.intToFloat(ArrayUtil.range(frameSize));
        float[] expectedResampledFrame = ArrayUtil.range(linearInterpolator.getResampleFrameSize(), 0, stretchFactor);
        float[] resampledFrame = new float[linearInterpolator.getResampleFrameSize()];

        linearInterpolator.resample(frame, resampledFrame);
        Assert.assertArrayEquals(expectedResampledFrame, resampledFrame, tolerance);
    }
}
