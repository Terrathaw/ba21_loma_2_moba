package ch.zhaw.ch.dsp;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ch.zhaw.ch.util.ArrayUtil;

public class ComplexFrameTest {

    @Test
    public void cartesianToPolar_isCorrect() {
        float tolerance = 0.000001f;
        float[] real = new float[]{20.25f, -12.5781555f, 1.8889709f, 0.43851632f, 0.10910845f, 0.0331192f};
        float[] imag = new float[]{0.0f, 1.0109414f, -1.0146414f, 0.03714776f, 0.07313982f, 0.0f};
        float[] expectedMagnitude = new float[]{20.25f, 12.618716f, 2.1442266f, 0.44008693f, 0.13135481f, 0.0331192f};
        float[] expectedPhase = new float[]{0.0f, 3.0613923f, -0.49291614f, 0.08451061f, 0.5905417f, 0.0f};
        ComplexFrame complexFrame = new ComplexFrame(real, imag);

        float[] magnitude = complexFrame.getMagnitude();
        float[] phase = complexFrame.getPhase();
        Assert.assertArrayEquals(expectedMagnitude, magnitude, tolerance);
        Assert.assertArrayEquals(expectedPhase, phase, tolerance);
    }

    @Test
    public void polarToCartesian_isCorrect() {
        float tolerance = 0.000001f;
        float[] expectedReal = new float[]{20.25f, -12.5781555f, 1.8889709f, 0.43851632f, 0.10910845f, 0.0331192f};
        float[] expectedImag = new float[]{0.0f, 1.0109414f, -1.0146414f, 0.03714776f, 0.07313982f, 0.0f};
        float[] magnitude = new float[]{20.25f, 12.618716f, 2.1442266f, 0.44008693f, 0.13135481f, 0.0331192f};
        float[] phase = new float[]{0.0f, 3.0613923f, -0.49291614f, 0.08451061f, 0.5905417f, 0.0f};
        ComplexFrame complexFrame = new ComplexFrame(expectedReal, expectedImag);
        complexFrame.setPhase(phase);
        complexFrame.setMagnitude(magnitude);

        float[] real = complexFrame.getReal();
        float[] imag = complexFrame.getImag();
        Assert.assertArrayEquals(expectedReal, real, tolerance);
        Assert.assertArrayEquals(expectedImag, imag, tolerance);
    }

    @Test
    public void polarToCartesian_fullFrame_isCorrect() {
        float tolerance = 0.0001f;
        int resolution = 2048;
        int resolutionNyquist = resolution / 2 + 1;
        float[] magnitude = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));
        float[] phase = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));
        float[] expectedReal = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));
        float[] expectedImag = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));


        try (BufferedReader fft_pol_reader = new BufferedReader(new FileReader("src\\test\\res\\fft_pol.csv"));
             BufferedReader fft_cart = new BufferedReader(new FileReader("src\\test\\res\\fft_cart.csv"))) {
            String[] line = fft_pol_reader.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                magnitude[i] = Float.parseFloat(line[i]);
            }

            line = fft_pol_reader.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                phase[i] = Float.parseFloat(line[i]);
            }

            line = fft_cart.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                expectedReal[i] = Float.parseFloat(line[i]);
            }

            line = fft_cart.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                expectedImag[i] = Float.parseFloat(line[i]);
            }

            ComplexFrame complexFrame = new ComplexFrame(expectedReal, expectedImag);
            complexFrame.setPhase(phase);
            complexFrame.setMagnitude(magnitude);

            float[] real = complexFrame.getReal();
            float[] imag = complexFrame.getImag();
            Assert.assertArrayEquals(expectedReal, real, tolerance);
            Assert.assertArrayEquals(expectedImag, imag, tolerance);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void cartesianToPolar_fullFrame_isCorrect() {
        float tolerance = 0.000001f;
        int resolution = 2048;
        int resolutionNyquist = resolution / 2 + 1;
        float[] expectedMagnitude = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));
        float[] expectedPhase = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));
        float[] real = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));
        float[] imag = ArrayUtil.intToFloat(ArrayUtil.range(resolutionNyquist));


        try (BufferedReader fft_pol_reader = new BufferedReader(new FileReader("src\\test\\res\\fft_pol.csv"));
             BufferedReader fft_cart = new BufferedReader(new FileReader("src\\test\\res\\fft_cart.csv"))) {
            String[] line = fft_pol_reader.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                expectedMagnitude[i] = Float.parseFloat(line[i]);
            }

            line = fft_pol_reader.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                expectedPhase[i] = Float.parseFloat(line[i]);
            }

            line = fft_cart.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                real[i] = Float.parseFloat(line[i]);
            }

            line = fft_cart.readLine().split(",");
            for (int i = 0; i < resolutionNyquist; i++) {
                imag[i] = Float.parseFloat(line[i]);
            }

            ComplexFrame complexFrame = new ComplexFrame(real, imag);

            float[] magnitude = complexFrame.getMagnitude();
            float[] phase = complexFrame.getPhase();
            Assert.assertArrayEquals(expectedMagnitude, magnitude, tolerance);
            Assert.assertArrayEquals(expectedPhase, phase, tolerance);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
