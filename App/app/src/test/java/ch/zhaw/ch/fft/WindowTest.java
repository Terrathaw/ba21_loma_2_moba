package ch.zhaw.ch.fft;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import ch.zhaw.ch.util.ArrayUtil;

public class WindowTest {

    @Test
    public void hann_normalizeWindowOverlap4_isCorrect() {
        float tolerance = 0.0011f;
        Window window = new Window(WindowType.HANN);
        int windowSize = 2048;
        int hopSizeFactor = 4;
        int hopSize = windowSize / hopSizeFactor;
        int sampleCount = 3 * windowSize;
        float[] samples = ArrayUtil.rangeOfValue(sampleCount, 1f);
        float[] windowedSamples = ArrayUtil.rangeOfValue(sampleCount, 0f);

        for (int index = 0; index < samples.length - windowSize; index += hopSize) {
            float[] frame = Arrays.copyOfRange(samples, index, index + windowSize);
            ArrayUtil.mul2(frame, window.getWindow(windowSize));
            ArrayUtil.mul2(frame, window.getWindow(windowSize));
            for (int i = 0; i < frame.length; i++) {
                windowedSamples[index + i] += window.getWindow(windowSize)[i];
            }
        }

        float[] centerWindow = Arrays.copyOfRange(windowedSamples, windowSize - 1, 2 * windowSize - 1);
        float[] referenceSamples = ArrayUtil.rangeOfValue(windowSize, (float) hopSizeFactor / 2);

        Assert.assertArrayEquals(referenceSamples, centerWindow, tolerance);
    }

    @Test
    public void hann_normalizeWindowOverlap12_isCorrect() {
        float tolerance = 0.027f;
        Window window = new Window(WindowType.HANN);
        int windowSize = 2048;
        int hopSizeFactor = 12;
        int hopSize = windowSize / hopSizeFactor;
        int sampleCount = 3 * windowSize;
        float[] samples = ArrayUtil.rangeOfValue(sampleCount, 1f);
        float[] windowedSamples = ArrayUtil.rangeOfValue(sampleCount, 0f);

        for (int index = 0; index < samples.length - windowSize; index += hopSize) {
            float[] frame = Arrays.copyOfRange(samples, index, index + windowSize);
            ArrayUtil.mul2(frame, window.getWindow(windowSize));
            ArrayUtil.mul2(frame, window.getWindow(windowSize));
            for (int i = 0; i < frame.length; i++) {
                windowedSamples[index + i] += window.getWindow(windowSize)[i];
            }
        }

        float[] centerWindow = Arrays.copyOfRange(windowedSamples, windowSize - 1, 2 * windowSize - 1);
        float[] referenceSamples = ArrayUtil.rangeOfValue(windowSize, (float) hopSizeFactor / 2);

        Assert.assertArrayEquals(referenceSamples, centerWindow, tolerance);
    }

    @Test
    public void window() {
        float[] hann = new Window(WindowType.HANN).getWindow(16);
    }

}
