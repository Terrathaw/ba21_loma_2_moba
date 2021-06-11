package ch.zhaw.ch.dsp;

import android.util.Log;

import ch.zhaw.ch.ModuleInfo;
import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.TrackInfo;
import ch.zhaw.ch.dsp.phase.TransientDetectionType;
import ch.zhaw.ch.dsp.resample.Resampler;
import ch.zhaw.ch.dsp.phase.PhaseResetType;
import ch.zhaw.ch.dsp.phase.PhaseShifter;
import ch.zhaw.ch.fft.FFT;
import ch.zhaw.ch.fft.Window;
import ch.zhaw.ch.util.ArrayUtil;

/***
 * Provides an audioTransformer that operates in the blockwise approach described in http://dafx.de/DAFX_Book_Page/index.html.
 */

public class PitchShifter implements AudioTransformer {
    private FFT fft;
    private Resampler resampler;
    private PhaseShifter phaseShifter;
    private FrameBuffer inputBuffer;
    private FrameBuffer outputBuffer;
    private float signalRescaleFactor;

    private TrackInfo info;
    private Window window;

    private float[] frame;
    private float[] resampledFrame;

    @Override
    public void init(ModuleInfo moduleInfo, TrackInfo trackInfo, SourceInfo sourceInfo) throws Exception {
        this.info = trackInfo;

        fft = FFT.getNewInstance(moduleInfo.getFft());
        phaseShifter = PhaseShifter.getPhaseShifter(moduleInfo.getPhaseShifter());
        Log.v("PitchShifter",moduleInfo.getPhaseShifter() );
        resampler = Resampler.getPhaseShifter(moduleInfo.getResampler());

        fft.init(info.getFrameSize());

        window = new Window(info.getWindowType());
        phaseShifter.init(info.getFrameSize(), info.getFrameSizeNyquist(), info.getHopSizeAnalysis(), info.getStretchFactor(), sourceInfo.getSampleRate(), TransientDetectionType.getValue(moduleInfo.getTransientDetector()), PhaseResetType.getValue(moduleInfo.getPhaseResetType()));
        resampler.init(info.getFrameSize(), info.getStretchFactor());
        frame = new float[info.getFrameSize()];
        resampledFrame = new float[resampler.getResampleFrameSize()];
        signalRescaleFactor = ArrayUtil.sum(ArrayUtil.mul(window.getWindow(info.getFrameSize()),window.getWindow(info.getFrameSize())))/info.getHopSizeSynthesis();

        inputBuffer = new FrameBuffer(info.getSampleBufferSize() * 4, 0);
        outputBuffer = new FrameBuffer(info.getSampleBufferSize() * 4, 0);
    }

    @Override
    public float[] transformBuffered(float[] samples) {
        inputBuffer.write(samples, samples.length, samples.length, false);
        while (inputBuffer.size() >= info.getFrameSize()) {
            outputBuffer.write(transformFrame(inputBuffer.read(info.getFrameSize(), info.getHopSizeAnalysis(), false)), resampler.getResampleFrameSize(), info.getHopSizeAnalysis(), true);
        }
        int readLength = Math.max(0, outputBuffer.size() - info.getFrameSize());
        float[] samplesTransformed = outputBuffer.read(readLength, readLength, true);
        ArrayUtil.div2(samplesTransformed, signalRescaleFactor); //normalize audio based on hop size factor to prevent clipping / overflow
        return samplesTransformed;
    }

    @Override
    public float[] transformFrame(float[] frame) {
        ArrayUtil.mul2(frame, window.getWindow(info.getFrameSize()));
        ComplexFrame fftFrame = fft.forward(frame);
        float[] phaseTransformed = phaseShifter.shift(fftFrame);
        fftFrame.setPhase(phaseTransformed);
        float[] frameTransformed = fft.inverse(fftFrame);
        ArrayUtil.mul2(frameTransformed, window.getWindow(info.getFrameSize()));
        resampler.resample(frameTransformed, resampledFrame);
        return resampledFrame;
    }

    @Override
    public float[] test(float[] samples) {
        inputBuffer.write(samples, samples.length, samples.length, false);
        while (inputBuffer.size() >= info.getFrameSize()) {
            frame = inputBuffer.read(info.getFrameSize(), info.getHopSizeAnalysis(), false);
            ArrayUtil.mul2(frame, window.getWindow(info.getFrameSize()));
            ComplexFrame fftFrame = fft.forward(frame);
            float[] phaseTransformed = phaseShifter.shift(fftFrame);
            fftFrame.setPhase(phaseTransformed);
            frame = fft.inverse(fftFrame);
            ArrayUtil.mul2(frame, window.getWindow(info.getFrameSize()));

            outputBuffer.write(frame, info.getFrameSize(), info.getHopSizeAnalysis(), true);
        }
        int readLength = outputBuffer.size() - info.getFrameSize();
        return outputBuffer.read(readLength, readLength, true);
    }


}
