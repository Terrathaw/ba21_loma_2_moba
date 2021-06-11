package ch.zhaw.ch;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.breakfastquay.rubberband.RubberBandStretcher;
import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.HashMap;

import ch.zhaw.ch.dsp.AudioTransformer;
import ch.zhaw.ch.gui.PlayerView;
import ch.zhaw.ch.gui.SongInfo;
import ch.zhaw.ch.route.Handler;
import ch.zhaw.ch.route.HandlerThread;
import ch.zhaw.ch.route.RouteHandler;
import ch.zhaw.ch.route.SinkHandler;
import ch.zhaw.ch.route.SourceHandler;
import ch.zhaw.ch.route.SyncedCycleBuffer;
import ch.zhaw.ch.visualization.FrequencyVisualizer;

/***
 * @author: lopezale
 * Provides a Android Service to run all playback functionalities in the background. This class also starts the Source-, Route-, Sink- and AllInOneHandler (and utility classes) and provides the DOM playback functions like song pause and restart.
 */
public class MusicService extends Service {
    private final String TAG = MusicService.class.getSimpleName();

    private Config config;
    private ArrayList<SongInfo> songInfos;
    private final IBinder musicBind = new MusicBinder();

    private SourceHandler sourceHandler;
    private RouteHandler routeHandler;
    private SinkHandler sinkHandler;

    private SyncedCycleBuffer inputBuffer;
    private SyncedCycleBuffer outputBuffer;
    private AudioTransformer transformer;
    private FrequencyVisualizer visualizer;

    private HashMap<Handler, HandlerThread> handlerThreads = new HashMap<>();

    private PlayerView playerView;

    //current position
    private int songPosn;
    private int cycleSize = 3;
    private boolean playingSong = false;
    private boolean paused = false;

    @Override
    public void onCreate() {
        super.onCreate();
        config = Config.getInstance();
        songPosn = 0;

        outputBuffer = new SyncedCycleBuffer("OutputBuffer");
        inputBuffer = new SyncedCycleBuffer("InputBuffer");

        sourceHandler = new SourceHandler(this, inputBuffer);
        routeHandler = new RouteHandler(this, inputBuffer, outputBuffer);
        sinkHandler = new SinkHandler(this, outputBuffer);

        handlerThreads.put(sourceHandler, new HandlerThread("SourceHandlerThread", sourceHandler));
        handlerThreads.put(routeHandler, new HandlerThread("RouteHandlerThread", routeHandler));
        handlerThreads.put(sinkHandler, new HandlerThread("SinkHandlerThread", sinkHandler));

        // Use for testing with single thread
//        AllInOneHandler allInOneHandler = new AllInOneHandler(sourceHandler, routeHandler, sinkHandler, inputBuffer, outputBuffer);
//        handlerThreads.put(allInOneHandler, new HandlerThread("AllInOneHandlerThread", allInOneHandler));

    }

    public void setList(ArrayList<SongInfo> theSongInfos) {
        Log.v(TAG, "Set SongList");
        songInfos = theSongInfos;
    }

    public void applySettings() {
        Log.v(TAG, "apply Settings");
        TrackInfo trackInfo = config.getTransform();
        ModuleInfo moduleInfo = config.getModule();
        trackInfo.init();
        routeHandler.setTrackInfoShadow(trackInfo);
        routeHandler.setModuleInfoShadow(moduleInfo);

        handlerThreads.get(routeHandler).initializeHandler();
    }

    public void restartSong() {
        playSong(this.songPosn);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopPlayback();
        return false;
    }

    public void playSong(int songIndex) {
        this.songPosn = songIndex;
        SongInfo songInfo = songInfos.get(songIndex);

        try {

            TrackInfo trackInfo = config.getTransform();
            SourceInfo sourceInfo = config.getSource();
            ModuleInfo moduleInfo = config.getModule();

            sourceHandler.setTrackInfoShadow(trackInfo);
            sourceHandler.setModuleInfoShadow(moduleInfo);
            sourceHandler.setSongInfoShadow(songInfo);
            sourceHandler.setSourceInfoShadow(sourceInfo);

            routeHandler.setModuleInfoShadow(moduleInfo);
            routeHandler.setSourceInfoShadow(sourceInfo);
            routeHandler.setTrackInfoShadow(trackInfo);

            sinkHandler.setModuleInfoShadow(moduleInfo);
            sinkHandler.setSourceInfoShadow(sourceInfo);

            paused = false;
            playingSong = true;
            handlerThreads.values().stream().filter(t -> t != null).forEach(t -> t.initializeHandler());
            handlerThreads.values().stream().filter(t -> t != null && !t.isAlive()).forEach(t -> t.start());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlayback() {
        playingSong = false;
        handlerThreads.values().stream().filter(t -> t != null).forEach(t -> t.stopHandler());
    }

    public void pausePlayback() {
        paused = true;
        handlerThreads.values().stream().filter(t -> t != null).forEach(t -> t.pauseHandler());
    }

    public void resumePlayback() {
        paused = false;
        handlerThreads.values().stream().filter(t -> t != null).forEach(t -> t.resumeHandler());
    }

    public void togglePlaySong() {
        if (paused) resumePlayback();
        else pausePlayback();
    }

    public void setGraph(GraphView graph) {
        if (visualizer != null)
            visualizer.setGraph(graph);
    }

    public void unsetGraph() {
        if (visualizer != null) {
            visualizer.setGraph(null);
        }
    }


    public SongInfo getCurrentSong() {
        if (sinkHandler != null) {
            return songInfos.get(songPosn);
        }
        return null;
    }

    public boolean playingSong() {
        return playingSong;
    }

}
