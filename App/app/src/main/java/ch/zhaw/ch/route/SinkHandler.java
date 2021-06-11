package ch.zhaw.ch.route;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

import ch.zhaw.ch.ModuleInfo;
import ch.zhaw.ch.MusicService;
import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.io.Sink;
import ch.zhaw.ch.util.PCMUtil;
import ch.zhaw.ch.visualization.Visualizer;

/***
 * Reads samples from the output cycle to be played back by the audiotrack.
 */

public class SinkHandler implements Handler {

    private static final String TAG = SinkHandler.class.getSimpleName();

    private final MusicService context;
    private final SyncedCycleBuffer outputBuffer;
    private final String outPath;

    private AudioTrack audioTrack;
    private Sink sink;
    private Visualizer visualizer;
    private float[] samples;

    private SourceInfo sourceInfo;
    private ModuleInfo moduleInfo;
    private SourceInfo sourceInfoShadow;
    private ModuleInfo moduleInfoShadow;

    public SinkHandler(MusicService context, SyncedCycleBuffer outputBuffer) {
        this.context = context;
        this.outputBuffer = outputBuffer;
        this.outPath = context.getExternalFilesDir(null).getPath();
    }

    @Override
    public boolean init() throws Exception {
        close();
        sourceInfo = sourceInfoShadow;
        moduleInfo = moduleInfoShadow;

        if (!outputBuffer.init(SyncedCycleBuffer.SYNCED_CYCLE_BUFFER_SIZE, sourceInfo.getByteBufferSize())) return false;

        visualizer = Visualizer.getNewInstance(moduleInfo.getVisualizer());
        sink = Sink.getNewInstance(moduleInfo.getSink());
        sink.open(outPath, sourceInfo.getName());
        sink.writeHeader(sourceInfo);
        Log.v(TAG, "wavwriter is init "+sink.isInitialized());
        audioTrack = setupAudioTrack();
        if (!isAudioTrackInitialized()) {
            Log.e(TAG, "Failed to initialize audio track");
            return false;
        }
        audioTrack.play();
        return true;
    }

    @Override
    public boolean ready() {
        return outputBuffer.checkIfBufferAvailable() && outputBuffer.tryLock();
    }

    @Override
    public void process() {
        samples = outputBuffer.getNextBuffer();

        if (visualizer != null) {
            float[] data = new float[samples.length];
            System.arraycopy(samples, 0, data, 0, samples.length);
            visualizer.displaySamples(data);
        }

        audioTrack.write(PCMUtil.float2ShortArray(samples), 0, samples.length);
        //Log.e(TAG, String.format("underrun: %d", audioTrack.getUnderrunCount()));

        if (sink.isInitialized()) {
            try {
                sink.write(samples);
            } catch (IOException e) {
                Log.e(TAG, ExceptionUtils.getStackTrace(e));
            }
        }
    }

    @Override
    public void close() throws IOException {
        closeAudioTrack();
        if (sink != null) sink.close();
    }

    @Override
    public void pause() {
        if (audioTrack != null) audioTrack.pause();
    }

    @Override
    public void resume() {
        if (audioTrack != null) audioTrack.play();
    }

    public boolean isAudioTrackInitialized() {
        return audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED;
    }

    public void closeAudioTrack() {
        if (!isAudioTrackInitialized()) return;
        audioTrack.flush();
        audioTrack.stop();
        audioTrack.release();
    }

    private AudioTrack setupAudioTrack() {
        return new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sourceInfo.getSampleRate())
                        .setChannelMask(sourceInfo.getChannelConfig())
                        .build())
                .setBufferSizeInBytes(sourceInfo.getByteBufferSize())
                .build();
    }


    public void setSourceInfoShadow(SourceInfo sourceInfoShadow) {
        this.sourceInfoShadow = sourceInfoShadow;
    }

    public void setModuleInfoShadow(ModuleInfo moduleInfoShadow) {
        this.moduleInfoShadow = moduleInfoShadow;
    }
}
