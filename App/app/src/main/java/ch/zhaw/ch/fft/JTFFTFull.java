package ch.zhaw.ch.fft;

import org.jtransforms.fft.FloatFFT_1D;

import java.util.Arrays;

import ch.zhaw.ch.dsp.ComplexFrame;

public class JTFFTFull implements FFT {
    private FloatFFT_1D fft;

    private int resolution;

    public void init(int resolution) {
        this.resolution = resolution;
        fft = new FloatFFT_1D(resolution);
    }

    public void init() {
        fft = new FloatFFT_1D(resolution);
    }

    @Override
    public ComplexFrame forward(float[] samples) {
        float[] arr = new float[resolution * 2];
        System.arraycopy(samples, 0, arr, 0, resolution);
        fft.realForwardFull(arr);
        return toComplexFrame(arr);
    }

    @Override
    public float[] inverse(ComplexFrame frame) {
        float[] arr = flatten(frame);
        fft.realInverseFull(arr, true);
        float[] samples = Arrays.copyOf(arr, resolution);
        return samples;
    }

    public ComplexFrame toComplexFrame(float[] fftValues) {
        int length = resolution;
        float[] real = new float[length];
        float[] imag = new float[length];

        for (int i = 0; i < resolution; i++) {
            real[i] = fftValues[2 * i];
            imag[i] = fftValues[2 * i + 1];
        }
        return new ComplexFrame(real, imag);
    }

    public float[] flatten(ComplexFrame frame) {
        float[] fftValues = new float[resolution * 2];
        for (int i = 0; i < resolution; i++) {
            fftValues[2 * i] = frame.getReal()[i];
            fftValues[2 * i + 1] = frame.getImag()[i];
        }
        return fftValues;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }
}
