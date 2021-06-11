package ch.zhaw.ch.dsp;

/***
 * Stores the information given by the FFT about a vertical line and provides functionalities to access them.
 */

public class ComplexFrame {

    private float[] real;
    private float[] imag;
    private float[] magnitude;
    private float[] phase;

    public ComplexFrame(float[] real, float[] imag) {
        this.real = real;
        this.imag = imag;
    }

    public float[] getReal() {
        if (real == null) calcCartesianCoordinates();
        return real;
    }

    public float[] getImag() {
        if (imag == null) calcCartesianCoordinates();
        return imag;
    }

    public float[] getMagnitude() {
        if (magnitude == null) calcPolarCoordinates();
        return magnitude;
    }

    public float[] getPhase() {
        if (phase == null) calcPolarCoordinates();
        return phase;
    }

    public void setReal(float[] real) {
        this.magnitude = null;
        this.phase = null;
        this.real = real;
    }

    public void setImag(float[] imag) {
        this.magnitude = null;
        this.phase = null;
        this.imag = imag;
    }

    public void setMagnitude(float[] magnitude) {
        this.real = null;
        this.imag = null;
        this.magnitude = magnitude;
    }

    public void setPhase(float[] phase) {
        this.real = null;
        this.imag = null;
        this.phase = phase;
    }

    private void calcCartesianCoordinates() {
        real = new float[phase.length];
        imag = new float[phase.length];
        for (int i = 0; i < getPhase().length; i++) {
            real[i] = (float) (magnitude[i] * Math.cos(phase[i]));
            imag[i] = (float) (magnitude[i] * Math.sin(phase[i]));
        }
    }

    private void calcPolarCoordinates() {
        magnitude = new float[real.length];
        phase = new float[real.length];
        for (int i = 0; i < real.length; i++) {
            magnitude[i] = (float) Math.sqrt(real[i] * real[i] + imag[i] * imag[i]);
            phase[i] = (float) Math.atan2(imag[i], real[i]);
        }
    }
}

