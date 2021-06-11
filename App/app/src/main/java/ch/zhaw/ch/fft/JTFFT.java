package ch.zhaw.ch.fft;

import org.jtransforms.fft.FloatFFT_1D;

import ch.zhaw.ch.dsp.ComplexFrame;

public class JTFFT implements FFT {
    private FloatFFT_1D fft;

    private int resolution;

    public void init(int resolution) {
        this.resolution = resolution;
        fft = new FloatFFT_1D(resolution);
    }

    @Override
    public ComplexFrame forward(float[] samples) {
        fft.realForward(samples);
        return toComplexFrame(samples);
    }

    @Override
    public float[] inverse(ComplexFrame frame) {
        float[] samples = flatten(frame);
        fft.realInverse(samples, true);
        return samples;
    }

    public ComplexFrame toComplexFrame(float[] fftValues) {
        //+1 because the first two fftValues reference a complex number each
        int length = resolution / 2 + 1;
        float[] real = new float[length];
        float[] imag = new float[length];

        //bin 0 and n/2 always have an imaginary value of 0
        real[0] = fftValues[0];
        imag[0] = 0;
        real[length - 1] = fftValues[1];
        imag[length - 1] = 0;
        for (int i = 1; i < resolution / 2; i++) {
            real[i] = fftValues[2 * i];
            imag[i] = fftValues[2 * i + 1];
        }
        return new ComplexFrame(real, imag);
    }

    public float[] flatten(ComplexFrame frame) {
        float[] fftValues = new float[resolution];
        fftValues[0] = frame.getReal()[0];
        fftValues[1] = frame.getReal()[frame.getReal().length - 1];
        for (int i = 1; i < resolution / 2; i++) {
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
