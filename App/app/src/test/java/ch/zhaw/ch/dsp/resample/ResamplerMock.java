package ch.zhaw.ch.dsp.resample;

public class ResamplerMock implements Resampler {

    private int resampleFrameSize;
    private float[] resampledFrame;

    public float[] getResampledFrame() {
        return resampledFrame;
    }

    public void setResampledFrame(float[] resampledFrame) {
        this.resampledFrame = resampledFrame;
    }

    @Override
    public void init(int frameSize, float stretchFactor) {
        resampleFrameSize = (int) Math.floor(frameSize / stretchFactor);
    }

    @Override
    public int getResampleFrameSize() {
        return resampleFrameSize;
    }

    @Override
    public void resample(float[] frame, float[] resampledFrame) {
        resampledFrame = this.resampledFrame;
    }
}
