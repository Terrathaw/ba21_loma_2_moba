package ch.zhaw.ch.route;

import android.util.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import ch.zhaw.ch.ConfigHandler;
import ch.zhaw.ch.ModuleInfo;
import ch.zhaw.ch.MusicService;
import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.TrackInfo;
import ch.zhaw.ch.dsp.AudioTransformer;
import ch.zhaw.ch.util.PCMUtil;

/***
 * Reads data from a input cycle buffer, transforms them with a given vocoder and writes the result to an output cycle.
 */

public class RouteHandler implements Handler, PropertyChangeListener {
    private static final String TAG = RouteHandler.class.getSimpleName();
    private MusicService context;

    private ArrayList<AudioTransformer> transformerChannels;
    private final SyncedCycleBuffer inputBuffer;
    private final SyncedCycleBuffer outputBuffer;
    private State currentState;
    private float[] samples;
    private float[][] sampleChannels;
    private float[][] sampleChannels2;

    private TrackInfo trackInfo;
    private SourceInfo sourceInfo;
    private ModuleInfo moduleInfo;
    private TrackInfo trackInfoShadow;
    private SourceInfo sourceInfoShadow;
    private ModuleInfo moduleInfoShadow;

    public enum State {
        GETINPUTLOCK,
        GETINPUTBUFFER,
        GETOUTPUTLOCK,
        WRITEOUTPUTBUFFER
    }

    public RouteHandler(MusicService context, SyncedCycleBuffer inputBuffer, SyncedCycleBuffer outputBuffer) {
        this.context = context;
        this.inputBuffer = inputBuffer;
        this.outputBuffer = outputBuffer;
        this.transformerChannels = new ArrayList<>();
        ConfigHandler.getInstance().addChangeListener(this);
    }

    @Override
    public boolean init() throws Exception {
        trackInfo = trackInfoShadow;
        sourceInfo = sourceInfoShadow;
        moduleInfo = moduleInfoShadow;

        transformerChannels.clear();
        sampleChannels = new float[sourceInfo.getNumberOfChannels()][];
        sampleChannels2 = new float[sourceInfo.getNumberOfChannels()][];
        for (int i = 0; i < sourceInfo.getNumberOfChannels(); i++) {
            transformerChannels.add(AudioTransformer.getNewInstance(moduleInfo.getTransformer()));
            sampleChannels[i] = new float[trackInfo.getChannelBufferSize()];
        }

        for (AudioTransformer transformer : transformerChannels) {
            transformer.init(moduleInfo, trackInfo, sourceInfo);
        }
        currentState = State.GETINPUTLOCK;
        return true;
    }

    @Override
    public boolean ready() {
        switch (currentState) {
            case GETINPUTLOCK:
                return inputBuffer.checkIfBufferAvailable() && inputBuffer.tryLock();
            case GETOUTPUTLOCK:
                return outputBuffer.hasAvailableSpace() && outputBuffer.tryLock();
            default:
                return false;
        }
    }

    @Override
    public void process() {

        switch (currentState) {
            case GETINPUTLOCK:
                samples = inputBuffer.getNextBuffer();
                PCMUtil.fillSampleChannels(samples, sampleChannels);
                for (int i = 0; i < transformerChannels.size(); i++) {
                    sampleChannels2[i] = transformerChannels.get(i).transformBuffered(sampleChannels[i]);
                }
                samples = PCMUtil.flattenSampleChannels(sampleChannels2);
                currentState = State.GETOUTPUTLOCK;
                break;
            case GETOUTPUTLOCK:
                outputBuffer.writeNextBuffer(samples);
                currentState = State.GETINPUTLOCK;
                break;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Log.v(TAG, evt.toString());
    }

    public void setTrackInfoShadow(TrackInfo trackInfoShadow) {
        this.trackInfoShadow = trackInfoShadow;
    }

    public void setSourceInfoShadow(SourceInfo sourceInfoShadow) {
        this.sourceInfoShadow = sourceInfoShadow;
    }

    public void setModuleInfoShadow(ModuleInfo moduleInfoShadow) {
        this.moduleInfoShadow = moduleInfoShadow;
    }

}
