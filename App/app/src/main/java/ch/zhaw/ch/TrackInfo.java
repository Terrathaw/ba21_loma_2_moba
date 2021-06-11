package ch.zhaw.ch;

import android.util.Log;

import ch.zhaw.ch.fft.WindowType;

/***
 * This class contains all information for our Vocoder about a given track.
 */

public class TrackInfo {
    private static final String TAG = TrackInfo.class.getName();
    private int sampleBufferSize;
    private int channelBufferSize;

    private int frameSize;
    private int frameSizeNyquist;
    private int hopSizeFactor;
    private int hopSizeAnalysis;
    private int hopSizeSynthesis;
    private int halfToneStepsToShift;
    private float pitchShiftFactor;
    private float stretchFactor;
    private WindowType windowType;

    public TrackInfo() {
        this.frameSize = 2048;
        this.hopSizeFactor = 4;
        this.halfToneStepsToShift = 0;
        this.windowType = WindowType.HANN;
        init();
    }

    public void init() {
        frameSizeNyquist = frameSize / 2 + 1;
        pitchShiftFactor = (float) Math.pow(2, halfToneStepsToShift / 12f);
        hopSizeSynthesis = frameSize / hopSizeFactor;
        hopSizeAnalysis = (int) Math.round(hopSizeSynthesis / pitchShiftFactor);
        stretchFactor = hopSizeSynthesis / (float) hopSizeAnalysis;
    }

    public void log() {
        Log.v(TAG, "sampleBufferSize: \t" + sampleBufferSize);
        Log.v(TAG, "frameSize: \t" + frameSize);
        Log.v(TAG, "frameSizeNyquist: \t" + frameSizeNyquist);
        Log.v(TAG, "hopSizeFactor: \t" + hopSizeFactor);
        Log.v(TAG, "hopSizeAnalysis: \t" + hopSizeAnalysis);
        Log.v(TAG, "hopSizeSynthesis: \t" + hopSizeSynthesis);
        Log.v(TAG, "halfToneStepsToShift: \t" + halfToneStepsToShift);
        Log.v(TAG, "pitchShiftFactor: \t" + pitchShiftFactor);
        Log.v(TAG, "stretchFactor: \t" + stretchFactor);
        Log.v(TAG, "windowType: \t" + windowType.toString());
    }

    public int getSampleBufferSize() {
        return sampleBufferSize;
    }

    public int getChannelBufferSize() {
        return channelBufferSize;
    }

    public void setSampleBufferSize(int sampleBufferSize, int numberOfChannels) {
        this.sampleBufferSize = sampleBufferSize;
        this.channelBufferSize = sampleBufferSize / numberOfChannels;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public int getFrameSizeNyquist() {
        return frameSizeNyquist;
    }

    public void setFrameSizeNyquist(int frameSizeNyquist) {
        this.frameSizeNyquist = frameSizeNyquist;
    }

    public int getHopSizeFactor() {
        return hopSizeFactor;
    }

    public void setHopSizeFactor(int hopSizeFactor) {
        this.hopSizeFactor = hopSizeFactor;
    }

    public int getHopSizeAnalysis() {
        return hopSizeAnalysis;
    }

    public void setHopSizeAnalysis(int hopSizeAnalysis) {
        this.hopSizeAnalysis = hopSizeAnalysis;
    }

    public int getHopSizeSynthesis() {
        return hopSizeSynthesis;
    }

    public void setHopSizeSynthesis(int hopSizeSynthesis) {
        this.hopSizeSynthesis = hopSizeSynthesis;
    }

    public int getHalfToneStepsToShift() {
        return halfToneStepsToShift;
    }

    public void setHalfToneStepsToShift(int halfToneStepsToShift) {
        this.halfToneStepsToShift = halfToneStepsToShift;
    }

    public float getPitchShiftFactor() {
        return pitchShiftFactor;
    }

    public void setPitchShiftFactor(float pitchShiftFactor) {
        this.pitchShiftFactor = pitchShiftFactor;
    }

    public float getStretchFactor() {
        return stretchFactor;
    }

    public void setStretchFactor(float stretchFactor) {
        this.stretchFactor = stretchFactor;
    }

    public WindowType getWindowType() {
        return windowType;
    }

    public void setWindowType(WindowType windowType) {
        this.windowType = windowType;
    }
}
