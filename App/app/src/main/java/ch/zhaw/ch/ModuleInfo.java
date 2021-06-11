package ch.zhaw.ch;

import java.util.ArrayList;
import java.util.HashMap;

import ch.zhaw.ch.dsp.PitchShifter;
import ch.zhaw.ch.dsp.phase.TransientDetectionType;
import ch.zhaw.ch.dsp.resample.LinearInterpolator;
import ch.zhaw.ch.dsp.phase.BasicPhaseShifter;
import ch.zhaw.ch.dsp.phase.DynamicPhaseLockedShifter;
import ch.zhaw.ch.dsp.phase.ScaledPhaseLockedShifter;
import ch.zhaw.ch.dsp.phase.PhaseShifter;
import ch.zhaw.ch.dsp.phase.PhaseResetType;
import ch.zhaw.ch.fft.JTFFT;
import ch.zhaw.ch.io.WaveReader;
import ch.zhaw.ch.io.WaveWriter;
import ch.zhaw.ch.visualization.FrequencyVisualizer;
/***
 * Provides access to all usable modules and classes accessible by the Vocoders. This class helps initiating and passing classes e.g. through changes done in the class ConfigHandle.
 */
public class ModuleInfo {
    private String source;
    private String sink;
    private String transformer;
    private String fft;
    private String phaseShifter;
    private String phaseResetType;
    private String transientDetector;
    private String resampler;
    private String visualizer;

    private HashMap<String, ArrayList<String>> modules;

    public ModuleInfo() {
        source = WaveReader.class.getName();
        sink = WaveWriter.class.getName();
        transformer = PitchShifter.class.getName();
        fft = JTFFT.class.getName();
        phaseShifter = DynamicPhaseLockedShifter.class.getName();
        resampler = LinearInterpolator.class.getName();
        visualizer = FrequencyVisualizer.class.getName();
        transientDetector = TransientDetectionType.COMPOUND.getIdentifier();
        phaseResetType = PhaseResetType.BAND_LIMITED.getIdentifier();

        modules = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();
        list.add(DynamicPhaseLockedShifter.class.getName());
        list.add(BasicPhaseShifter.class.getName());
        list.add(ScaledPhaseLockedShifter.class.getName());
        modules.put(PhaseShifter.class.getName(), list);
    }

    public ArrayList<String> getModulesByInterface(String name) {
        return modules.get(name);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSink() {
        return sink;
    }

    public void setSink(String sink) {
        this.sink = sink;
    }

    public String getTransformer() {
        return transformer;
    }

    public void setTransformer(String transformer) {
        this.transformer = transformer;
    }

    public String getFft() {
        return fft;
    }

    public void setFft(String fft) {
        this.fft = fft;
    }

    public String getPhaseShifter() {
        return phaseShifter;
    }

    public void setPhaseShifter(String phaseShifter) {
        this.phaseShifter = phaseShifter;
    }

    public String getResampler() {
        return resampler;
    }

    public void setResampler(String resampler) {
        this.resampler = resampler;
    }

    public String getVisualizer() {
        return visualizer;
    }

    public void setVisualizer(String visualizer) {
        this.visualizer = visualizer;
    }

    public String getTransientDetector() {return transientDetector;}
    public void setTransientDetector(String transientDetector) {this.transientDetector = transientDetector; }

    public String getPhaseResetType() {return phaseResetType;}

    public void setPhaseResetType(String phaseResetType) {this.phaseResetType = phaseResetType;}
}
