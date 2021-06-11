package ch.zhaw.ch.route;

import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

import ch.zhaw.ch.ModuleInfo;
import ch.zhaw.ch.MusicService;
import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.TrackInfo;
import ch.zhaw.ch.gui.SongInfo;
import ch.zhaw.ch.io.Source;

/***
 * Reads sample wise data from the inputstream and writes it in a cycle buffer for other handlers to use.
 */

public class SourceHandler implements Handler {
    private static final String TAG = SourceHandler.class.getSimpleName();
    private MusicService context;
    private Source source;
    private Source sourceShadow;
    private SourceInfo sourceInfo;
    private ModuleInfo moduleInfo;
    private TrackInfo trackInfo;
    private SongInfo songInfo;
    private SourceInfo sourceInfoShadow;
    private ModuleInfo moduleInfoShadow;
    private TrackInfo trackInfoShadow;
    private SongInfo songInfoShadow;
    private SyncedCycleBuffer inputBuffer;
    private float[] sampleBuffer;

    public SourceHandler(MusicService context, SyncedCycleBuffer inputBuffer) {
        this.context = context;
        this.inputBuffer = inputBuffer;
        Log.v(TAG, "created");
    }

    @Override
    public boolean init() throws Exception {
        close();
        sourceInfo = sourceInfoShadow;
        moduleInfo = moduleInfoShadow;
        trackInfo = trackInfoShadow;
        songInfo = songInfoShadow;
        source = sourceShadow;

        if (!inputBuffer.init(SyncedCycleBuffer.SYNCED_CYCLE_BUFFER_SIZE, sourceInfo.getByteBufferSize())) return false;
        sampleBuffer = new float[trackInfo.getSampleBufferSize()];
        return true;
    }

    @Override
    public boolean ready() {
        return inputBuffer.hasAvailableSpace();
    }

    @Override
    public void process() throws IOException {
        if (source.read(sampleBuffer) > 0) {
            inputBuffer.waitForLock();
            inputBuffer.writeNextBuffer(sampleBuffer);
        }
    }

    @Override
    public void close() throws IOException {
        if (source != null) source.close();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    public void setSourceInfoShadow(SourceInfo sourceInfoShadow) {
        this.sourceInfoShadow = sourceInfoShadow;
        try {
            sourceShadow = Source.getNewInstance(moduleInfoShadow.getSource());
            sourceShadow.init(context.getContentResolver().openInputStream(songInfoShadow.getUri()));
            sourceShadow.setSourceInfo(sourceInfoShadow);
            sourceInfoShadow.setName(songInfo.getTitle());
        } catch (Exception e) {
            Log.e(TAG, ExceptionUtils.getStackTrace(e));
        }
    }

    public void setModuleInfoShadow(ModuleInfo moduleInfoShadow) {
        this.moduleInfoShadow = moduleInfoShadow;
    }

    public void setTrackInfoShadow(TrackInfo trackInfoShadow) {
        this.trackInfoShadow = trackInfoShadow;
    }

    public void setSongInfoShadow(SongInfo songInfoShadow) {
        this.songInfoShadow = songInfoShadow;
    }
}
