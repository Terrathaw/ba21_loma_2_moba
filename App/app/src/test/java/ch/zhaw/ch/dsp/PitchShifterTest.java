package ch.zhaw.ch.dsp;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import ch.zhaw.ch.ModuleInfo;
import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.TrackInfo;
import ch.zhaw.ch.dsp.resample.LinearInterpolator;
import ch.zhaw.ch.dsp.resample.Resampler;
import ch.zhaw.ch.dsp.phase.BasicPhaseShifter;
import ch.zhaw.ch.dsp.phase.PhaseShifter;
import ch.zhaw.ch.fft.FFT;
import ch.zhaw.ch.fft.JTFFT;
import ch.zhaw.ch.fft.WindowType;
import ch.zhaw.ch.java.audio.AudioFileUtil;
import ch.zhaw.ch.util.ArrayUtil;

public class PitchShifterTest {


    @Test
    public void transformSine_realTime_isCorrect() {
        float tolerance = 0.38f;
        ModuleInfo moduleInfo = new ModuleInfo();
        SourceInfo sourceInfo = new SourceInfo();
        sourceInfo.setSampleRate(22050);
        sourceInfo.setNumberOfChannels((short) 1);
        sourceInfo.setBitDepth((short) 16);
        TrackInfo info = new TrackInfo();
        info.setFrameSize(2048);
        info.setHopSizeFactor(4);
        info.setPitchShiftFactor(1.2f);
        info.setWindowType(WindowType.HANN);

        int sineFrequency = 440;
        int sineLength = 5;

        String filename = "simple-sine-real-time-%s_%s.wav";

        float[] timeline = ArrayUtil.range(sourceInfo.getSampleRate() * sineLength, 0, 1f / sourceInfo.getSampleRate());
        float[] sine = ArrayUtil.rangeOfValue(timeline.length + info.getFrameSize() * 2, 0f);
        float[] sineReference = ArrayUtil.rangeOfValue(timeline.length + info.getFrameSize() * 2, 0f);
        float[] sineTransformed = ArrayUtil.rangeOfValue(timeline.length + info.getFrameSize() * 3, 0f);
        info.setSampleBufferSize(sineTransformed.length, sourceInfo.getNumberOfChannels());

        System.arraycopy(timeline, 0, sine, info.getFrameSize(), timeline.length);
        System.arraycopy(timeline, 0, sineReference, info.getFrameSize(), timeline.length);
        for (int i = 0; i < sine.length; i++) {
            sine[i] = (float) Math.sin(2 * Math.PI * sineFrequency * sine[i]);
            sineReference[i] = (float) Math.sin(2 * Math.PI * sineFrequency * info.getStretchFactor() * sineReference[i]);
        }

        FFT fft = new JTFFT();

        PhaseShifter phaseShifter = new BasicPhaseShifter();
        Resampler resampler = new LinearInterpolator();
        PitchShifter pitchShifter = new PitchShifter();
        try {
            pitchShifter.init(moduleInfo, info, sourceInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sineTransformed = pitchShifter.transformBuffered(sine);

        // the buffered transform is not built to process partial frames. We therefore need more cutoff to prevent false negatives
        int croppedLength = timeline.length - info.getFrameSize();

        float[] croppedSine = Arrays.copyOfRange(sine, info.getFrameSize(), croppedLength);
        float[] croppedSineReference = Arrays.copyOfRange(sineReference, info.getFrameSize(), croppedLength);
        //cropped sine transformed signal doesn't start at phase 0 and needs a little offset
        float[] croppedSineTransformed = Arrays.copyOfRange(sineTransformed, info.getFrameSize() + 7, croppedLength + 7);

        AudioFileUtil.writeWav(String.format(filename, sineFrequency, "original"), croppedSine, sourceInfo.getSampleRate());
        AudioFileUtil.writeWav(String.format(filename, sineFrequency, "transformed"), croppedSineTransformed, sourceInfo.getSampleRate());
        AudioFileUtil.writeWav(String.format(filename, sineFrequency, "reference"), croppedSineReference, sourceInfo.getSampleRate());

        Assert.assertArrayEquals(croppedSineReference, croppedSineTransformed, tolerance);
    }

    @Test
    public void transformSine_fullTime_isCorrect() {
        float tolerance = 0.38f;
        ModuleInfo moduleInfo = new ModuleInfo();
        SourceInfo sourceInfo = new SourceInfo();
        sourceInfo.setSampleRate(22050);
        sourceInfo.setNumberOfChannels((short) 1);
        sourceInfo.setBitDepth((short) 16);
        TrackInfo info = new TrackInfo();
        info.setFrameSize(2048);
        info.setHopSizeFactor(4);
        info.setPitchShiftFactor(1.2f);
        info.setWindowType(WindowType.HANN);

        int sineFrequency = 440;
        int sineLength = 5;

        String filename = "simple-sine-real-time-%s_%s.wav";

        float[] timeline = ArrayUtil.range(sourceInfo.getSampleRate() * sineLength, 0, 1f / sourceInfo.getSampleRate());
        float[] sine = ArrayUtil.rangeOfValue(timeline.length + info.getFrameSize() * 2, 0f);
        float[] sineReference = ArrayUtil.rangeOfValue(timeline.length + info.getFrameSize() * 2, 0f);
        float[] sineTransformed = ArrayUtil.rangeOfValue(timeline.length + info.getFrameSize() * 3, 0f);
        info.setSampleBufferSize(sineTransformed.length, sourceInfo.getNumberOfChannels());

        System.arraycopy(timeline, 0, sine, info.getFrameSize(), timeline.length);
        System.arraycopy(timeline, 0, sineReference, info.getFrameSize(), timeline.length);
        for (int i = 0; i < sine.length; i++) {
            sine[i] = (float) Math.sin(2 * Math.PI * sineFrequency * sine[i]);
            sineReference[i] = (float) Math.sin(2 * Math.PI * sineFrequency * info.getStretchFactor() * sineReference[i]);
        }

        FFT fft = new JTFFT();

        PhaseShifter phaseShifter = new BasicPhaseShifter();
        Resampler resampler = new LinearInterpolator();
        PitchShifter pitchShifter = new PitchShifter();
        try {
            pitchShifter.init(moduleInfo, info, sourceInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int index = 0; index < sine.length - info.getFrameSize(); index += info.getHopSizeAnalysis()) {
            float[] frame = Arrays.copyOfRange(sine, index, index + info.getFrameSize());
            float[] resampledFrame = pitchShifter.transformFrame(frame);
            for (int i = 0; i < resampledFrame.length; i++) {
                sineTransformed[index + i] += resampledFrame[i];
            }
        }
        ArrayUtil.div2(sineTransformed, info.getHopSizeFactor() / 2f * (1 - Math.abs(1 - info.getPitchShiftFactor())));

        float[] croppedSine = Arrays.copyOfRange(sine, info.getFrameSize(), timeline.length);
        float[] croppedSineReference = Arrays.copyOfRange(sineReference, info.getFrameSize(), timeline.length);
        //cropped sine transformed signal doesn't start at phase 0 and needs a little offset
        int offset = 7;
        float[] croppedSineTransformed = Arrays.copyOfRange(sineTransformed, info.getFrameSize() + offset, timeline.length + offset);

        AudioFileUtil.writeWav(String.format(filename, sineFrequency, "original"), croppedSine, sourceInfo.getSampleRate());
        AudioFileUtil.writeWav(String.format(filename, sineFrequency, "transformed"), croppedSineTransformed, sourceInfo.getSampleRate());
        AudioFileUtil.writeWav(String.format(filename, sineFrequency, "reference"), croppedSineReference, sourceInfo.getSampleRate());

        Assert.assertArrayEquals(croppedSineReference, croppedSineTransformed, tolerance);
    }


}
