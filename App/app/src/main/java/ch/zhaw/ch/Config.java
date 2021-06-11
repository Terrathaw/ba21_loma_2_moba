package ch.zhaw.ch;

import android.util.Log;

import ch.zhaw.ch.dsp.PitchShifter;
import ch.zhaw.ch.dsp.RubberBand;
import ch.zhaw.ch.dsp.phase.TransientDetectionType;
import ch.zhaw.ch.dsp.phase.PhaseShifter;
import ch.zhaw.ch.dsp.phase.PhaseShifterModule;
import ch.zhaw.ch.dsp.phase.PhaseResetType;

/***
 * This class is initiated als singleton to store all needed information for the Vocoder. These configs can be manipulated through the DOM, chaning Vocoders and Pitchshift factors. Track and Song information will also be stored here to be more accessible throughout.
 */
public class Config {
    private static Config instance;
    private final String versionNumber = "0.0.3";

    public static synchronized Config getInstance() {
        if (Config.instance == null) {
            Config.instance = new Config();
        }
        return Config.instance;
    }

    public static final int MIN_BYTE_BUFFER_SIZE = 8192;

    private SourceInfo source;
    private TrackInfo transform;
    private ModuleInfo module;

    public Config() {
        this.source = new SourceInfo();
        this.transform = new TrackInfo();
        this.module = new ModuleInfo();
    }

    public void setByteBufferSize(int byteBufferSize) {
        source.setByteBufferSize(Math.max(transform.getFrameSize() * 4 * source.getNumberOfChannels(), byteBufferSize));
        transform.setSampleBufferSize(source.getSampleBufferSize(), source.getNumberOfChannels());
    }

    public void setPitchShiftAlgorithm(int moduleIndex) {
        PhaseShifterModule rubberband = PhaseShifterModule.getValue(RubberBand.class.getName());
        if(rubberband.getIndex() == moduleIndex){
            module.setTransformer(RubberBand.class.getName());
        }else {
            module.setTransformer(PitchShifter.class.getName());
            module.setPhaseShifter(module.getModulesByInterface(PhaseShifter.class.getName()).get(moduleIndex));
        }
    }

    public void setTransientDetectionType(int moduleIndex) {
        Log.v("config", moduleIndex+"set "+TransientDetectionType.getValue(moduleIndex).getIdentifier());
        module.setTransientDetector(TransientDetectionType.getValue(moduleIndex).getIdentifier());
    }

    public void setHalfToneStepsToShift(int halfToneStepsToShift) {
        transform.setHalfToneStepsToShift(halfToneStepsToShift);
    }

    public SourceInfo getSource() {
        return source;
    }

    public TrackInfo getTransform() {
        return transform;
    }

    public ModuleInfo getModule() {
        return module;
    }

    public String getVersionNumber() {
        return this.versionNumber;
    }

    public void setPhaseResetType(int moduleIndex) {
        module.setPhaseResetType(PhaseResetType.getValue(moduleIndex).getIdentifier());
    }
}
