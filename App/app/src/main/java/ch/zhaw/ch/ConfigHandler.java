package ch.zhaw.ch;

import android.util.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.ch.dsp.phase.TransientDetectionType;
import ch.zhaw.ch.dsp.phase.BasicPhaseShifter;
import ch.zhaw.ch.dsp.phase.DynamicPhaseLockedShifter;
import ch.zhaw.ch.dsp.phase.ScaledPhaseLockedShifter;

public class ConfigHandler {
    private final String TAG = ConfigHandler.class.getSimpleName();
    private static ConfigHandler instance;
    private ArrayList<PropertyChangeListener> listeners = new ArrayList<>();
    //Reflections reflections;
    private List<String> pitchShiftAlgorithms;
    private List<String> transientDetections;
    private int pitchShiftAlgorithm = 0;

    private int pitchShiftRatio = 0;

    private ConfigHandler() {
        Log.v("Config", "created");

        pitchShiftAlgorithms = new ArrayList<>();
        pitchShiftAlgorithms.add(BasicPhaseShifter.class.getSimpleName());
        pitchShiftAlgorithms.add(DynamicPhaseLockedShifter.class.getSimpleName());
        pitchShiftAlgorithms.add(ScaledPhaseLockedShifter.class.getSimpleName());

        transientDetections = new ArrayList<>();
        transientDetections.add(TransientDetectionType.NONE.name());
        transientDetections.add(TransientDetectionType.COMPOUND.name());
        transientDetections.add(TransientDetectionType.HIGH_FREQ.name());
        transientDetections.add(TransientDetectionType.PERCUSSIVE.name());

    }

    public static synchronized ConfigHandler getInstance() {
        if (ConfigHandler.instance == null) {
            ConfigHandler.instance = new ConfigHandler();
        }
        return ConfigHandler.instance;
    }

    public int getPitchShiftRatio() {
        return pitchShiftRatio;
    }

    public void setPitchShiftRatio(int pitchShiftRatio) {
        int oldValue = this.pitchShiftRatio;
        this.pitchShiftRatio = pitchShiftRatio;
        notifyListeners(this, "pitchShitfRatio", "" + oldValue, "" + this.pitchShiftRatio);
    }

    public List<String> getPhaseShifter() {
        return pitchShiftAlgorithms;
    }

    private void notifyListeners(Object object, String property, String oldValue, String newValue) {
        for (PropertyChangeListener name : listeners) {
            name.propertyChange(new PropertyChangeEvent(this, property, oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listeners.add(newListener);
        Log.v(TAG, "Listener added");
    }

    public int getPitchShiftAlgorithm() {
        return pitchShiftAlgorithm;
    }

    public void setPitchShiftAlgorithm(int pitchShiftAlgorithm) {
        this.pitchShiftAlgorithm = pitchShiftAlgorithm;
    }

}