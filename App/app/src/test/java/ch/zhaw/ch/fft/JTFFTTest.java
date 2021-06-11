package ch.zhaw.ch.fft;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.TrackInfo;
import ch.zhaw.ch.dsp.ComplexFrame;
import ch.zhaw.ch.java.audio.AudioFileUtil;
import ch.zhaw.ch.util.ArrayUtil;

import static org.junit.Assert.assertEquals;

public class JTFFTTest {

    @Test
    public void fft_custom_singleFrame_isCorrect() {
        float tolerance = 0.0000001f;
        int frameSize = 8;

        float[] sine = new float[]{0, 1, 2, 0, 0, 0, 0, 0};

        FFT fft = new JTFFT();
        fft.init(frameSize);

        ComplexFrame complexFrame = fft.forward(Arrays.copyOf(sine, sine.length));
        complexFrame.getMagnitude();
        complexFrame.setMagnitude(complexFrame.getMagnitude());
        float[] sineTransformed = fft.inverse(complexFrame);

        Assert.assertArrayEquals(sine, sineTransformed, tolerance);
    }

    @Test
    public void fft_sine_singleFrame_isCorrect() {
        float tolerance = 0.0000001f;

        int frameSize = 128;
        int sineFrequency = 8;
        int sineLength = 1;

        float[] sine = ArrayUtil.range(frameSize * sineLength, 0, 1f / frameSize);

        for (int i = 0; i < sine.length; i++) {
            sine[i] = (float) Math.sin(2 * Math.PI * sineFrequency * sine[i]);
        }

        FFT fft = new JTFFT();
        fft.init(frameSize);

        ComplexFrame complexFrame = fft.forward(Arrays.copyOf(sine, sine.length));
        complexFrame.setMagnitude(complexFrame.getMagnitude());
        float[] sineTransformed = fft.inverse(complexFrame);

        Assert.assertArrayEquals(sine, sineTransformed, tolerance);
    }

    @Test
    public void fft_sine_singleFrame_windowed_isCorrect() {
        float tolerance = 0.000001f;

        WindowType windowType = WindowType.HANN;
        int frameSize = 128;
        int sineFrequency = 8;
        int sineLength = 1;

        float[] sine = ArrayUtil.range(frameSize * sineLength, 0, 1f / frameSize);

        for (int i = 0; i < sine.length; i++) {
            sine[i] = (float) Math.sin(2 * Math.PI * sineFrequency * sine[i]);
        }

        Window window = new Window(windowType);
        FFT fft = new JTFFT();
        fft.init(frameSize);

        float[] frame = Arrays.copyOf(sine, sine.length);
        ArrayUtil.mul2(frame, window.getWindow(frameSize));
        ComplexFrame complexFrame = fft.forward(frame);
        complexFrame.setMagnitude(complexFrame.getMagnitude());
        float[] sineTransformed = fft.inverse(complexFrame);
        ArrayUtil.mul2(sineTransformed, window.getWindow(frameSize));

        ArrayUtil.mul2(sine, window.getWindow(frameSize));
        ArrayUtil.mul2(sine, window.getWindow(frameSize));
        Assert.assertArrayEquals(sine, sineTransformed, tolerance);
    }

    @Test
    public void forward_fullFrame_windowed_isCorrect() {
        int resolution = 2048;
        int resolutionNyquist = resolution / 2 + 1;
        float tolerance = 0.4f;
        WindowType windowType = WindowType.HANN;
        float[] frame = ArrayUtil.intToFloat(ArrayUtil.range(resolution));
        float[] expectedReal = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));
        float[] expectedImag = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));

        try (BufferedReader fft_in_reader = new BufferedReader(new FileReader("src\\test\\res\\fft_in.csv"));
             BufferedReader fft_out_reader = new BufferedReader(new FileReader("src\\test\\res\\fft_cart.csv"))) {
            String[] line = fft_in_reader.readLine().split(",");
            for (int i = 0; i < line.length; i++) {
                frame[i] = Float.parseFloat(line[i]);
            }

            line = fft_out_reader.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                expectedReal[i] = Float.parseFloat(line[i]);
            }

            line = fft_out_reader.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                expectedImag[i] = Float.parseFloat(line[i]);
            }

            Window window = new Window(windowType);
            FFT fft = new JTFFT();
            fft.init(resolution);

            ArrayUtil.mul2(frame, window.getWindow(resolution));
            ComplexFrame complexFrame = fft.forward(frame);

            Assert.assertArrayEquals(expectedReal, complexFrame.getReal(), tolerance);
            Assert.assertArrayEquals(expectedImag, complexFrame.getImag(), tolerance);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void inverse_fullFrame_windowed_isCorrect() {
        int resolution = 2048;
        int resolutionNyquist = resolution / 2 + 1;
        float tolerance = 0.3f;
        WindowType windowType = WindowType.HANN;
        float[] expectedSamples = ArrayUtil.intToFloat(ArrayUtil.range(resolution));
        float[] real = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));
        float[] imag = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));

        try (BufferedReader ifft_out_reader = new BufferedReader(new FileReader("src\\test\\res\\ifft_out.csv"));
             BufferedReader fft_cart_reader = new BufferedReader(new FileReader("src\\test\\res\\fft_cart.csv"))) {
            String[] line = ifft_out_reader.readLine().split(",");
            for (int i = 0; i < line.length; i++) {
                expectedSamples[i] = Float.parseFloat(line[i]);
            }

            line = fft_cart_reader.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                real[i] = Float.parseFloat(line[i]);
            }

            line = fft_cart_reader.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                imag[i] = Float.parseFloat(line[i]);
            }

            Window window = new Window(windowType);
            FFT fft = new JTFFT();
            fft.init(resolution);
            ComplexFrame complexFrame = new ComplexFrame(real, imag);
            float[] samples = fft.inverse(complexFrame);
            ArrayUtil.mul2(samples, window.getWindow(resolution));

            Assert.assertArrayEquals(expectedSamples, samples, tolerance);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void fft_sine_windowed_isCorrect() {
        float tolerance = 0.5f;
        SourceInfo sourceInfo = new SourceInfo();
        sourceInfo.setSampleRate(22528);
        sourceInfo.setNumberOfChannels((short) 1);
        sourceInfo.setBitDepth((short) 16);
        TrackInfo info = new TrackInfo();
        info.setFrameSize(2048);
        info.setHopSizeFactor(4);
        info.setPitchShiftFactor(1);
        info.setWindowType(WindowType.HANN);

        int sineFrequency = 440;
        int sineLength = 5;

        String filename = "simple-sine-fft-%s_%s.wav";


        float[] timeline = ArrayUtil.range(sourceInfo.getSampleRate() * sineLength, 0, 1f / sourceInfo.getSampleRate());
        float[] sine = ArrayUtil.rangeOfValue(timeline.length + info.getFrameSize() * 2, 0f);
        float[] sineTransformed = ArrayUtil.rangeOfValue(timeline.length + info.getFrameSize() * 2, 0f);

        System.arraycopy(timeline, 0, sine, info.getFrameSize(), timeline.length);

        for (int i = 0; i < sine.length; i++) {
            sine[i] = (float) Math.sin(2 * Math.PI * sineFrequency * sine[i]);
        }

        Window window = new Window(info.getWindowType());
        FFT fft = new JTFFT();
        fft.init(info.getFrameSize());

        for (int index = 0; index < sine.length - info.getFrameSize(); index += info.getHopSizeAnalysis()) {
            float[] frame = Arrays.copyOfRange(sine, index, index + info.getFrameSize());

            ArrayUtil.mul2(frame, window.getWindow(info.getFrameSize()));
            ComplexFrame complexFrame = fft.forward(frame);
            complexFrame.setMagnitude(complexFrame.getMagnitude());
            frame = fft.inverse(complexFrame);
            ArrayUtil.mul2(frame, window.getWindow(info.getFrameSize()));

            for (int i = 0; i < frame.length; i++) {
                sineTransformed[index + i] += frame[i];
            }
        }

        float[] croppedSine = Arrays.copyOfRange(sine, info.getFrameSize(), sine.length - info.getFrameSize());
        float[] croppedSineTransformed = Arrays.copyOfRange(sineTransformed, info.getFrameSize(), sineTransformed.length - info.getFrameSize());

        AudioFileUtil.writeWav(String.format(filename, sineFrequency, "original"), croppedSine, sourceInfo.getSampleRate());
        AudioFileUtil.writeWav(String.format(filename, sineFrequency, "transformed"), croppedSineTransformed, sourceInfo.getSampleRate());

        Assert.assertArrayEquals(croppedSine, croppedSineTransformed, tolerance);
    }


    @Test
    public void fft_sine_isCorrect() {
        float tolerance = 0.000001f;

        int sampeRate = 22528;
        int frameSize = 2048;

        int sineFrequency = 440;
        int sineLength = 5;

        String filename = "simple-sine-fft-%s_%s.wav";

        float[] sine = ArrayUtil.range(sampeRate * sineLength, 0, 1f / sampeRate);
        float[] sineTransformed = new float[sampeRate * sineLength];

        for (int i = 0; i < sine.length; i++) {
            sine[i] = (float) Math.sin(2 * Math.PI * sineFrequency * sine[i]);
        }

        FFT fft = new JTFFT();
        fft.init(frameSize);

        for (int index = 0; index < sine.length; index += frameSize) {
            float[] frame = Arrays.copyOfRange(sine, index, index + frameSize);

            ComplexFrame complexFrame = fft.forward(frame);
            complexFrame.setMagnitude(complexFrame.getMagnitude());
            frame = fft.inverse(complexFrame);

            System.arraycopy(frame, 0, sineTransformed, index + 0, frame.length);
        }
        float[] croppedSine = Arrays.copyOfRange(sine, frameSize, sine.length - frameSize);
        float[] croppedSineTransformed = Arrays.copyOfRange(sineTransformed, frameSize, sineTransformed.length - frameSize);

        AudioFileUtil.writeWav(String.format(filename, sineFrequency, "original"), croppedSine, sampeRate);
        AudioFileUtil.writeWav(String.format(filename, sineFrequency, "transformed"), croppedSineTransformed, sampeRate);

        Assert.assertArrayEquals(sine, sineTransformed, tolerance);
    }

    @Test
    public void toComplexFrame_isCorrect() {
        int resolution = 10;
        int resolutionNyquist = resolution / 2 + 1;
        WindowType windowType = WindowType.HANN;
        float[] fftValues = ArrayUtil.intToFloat(ArrayUtil.range(resolution));

        JTFFT fft = new JTFFT();
        fft.init(resolution);
        ComplexFrame frame = fft.toComplexFrame(fftValues);

        assertEquals(frame.getReal().length, resolutionNyquist);
        assertEquals(frame.getImag().length, resolutionNyquist);
        assertEquals(frame.getReal()[0], fftValues[0], 0.0);
        assertEquals(frame.getReal()[resolutionNyquist - 1], fftValues[1], 0.0);
        assertEquals(0, frame.getImag()[0], 0.0);
        assertEquals(0, frame.getImag()[resolutionNyquist - 1], 0.0);
        for (int i = 1; i < resolutionNyquist - 1; i++) {
            assertEquals(frame.getReal()[i], fftValues[i * 2], 0.0);
            assertEquals(frame.getImag()[i], fftValues[i * 2 + 1], 0.0);
        }
    }

    @Test
    public void flatten_isCorrect() {
        int resolution = 10;
        int resolutionNyquist = resolution / 2 + 1;
        WindowType windowType = WindowType.HANN;
        float[] real = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));
        float[] imag = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist, 10));
        ComplexFrame frame = new ComplexFrame(real, imag);

        JTFFT fft = new JTFFT();
        fft.init(resolution);
        float[] fftValues = fft.flatten(frame);

        assertEquals(fftValues.length, resolution);
        assertEquals(fftValues[0], real[0], 0.0);
        assertEquals(fftValues[1], real[resolutionNyquist - 1], 0.0);
        for (int i = 1; i < resolutionNyquist - 1; i++) {
            assertEquals(real[i], fftValues[i * 2], 0.0);
            assertEquals(imag[i], fftValues[i * 2 + 1], 0.0);
        }
    }
}
