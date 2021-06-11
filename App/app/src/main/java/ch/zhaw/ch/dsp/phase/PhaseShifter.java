package ch.zhaw.ch.dsp.phase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ch.zhaw.ch.dsp.ComplexFrame;

public interface PhaseShifter {

    void init(int frameSize, int frameSizeNyquist, int hopSizeAnalysis, float strechFactor, int sampleRate, TransientDetectionType transientDetectionType, PhaseResetType phaseResetType);

    float[] shift(ComplexFrame frame);

    static PhaseShifter getPhaseShifter(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<PhaseShifter> clazz = (Class<PhaseShifter>) Class.forName(classname);
        Constructor<PhaseShifter> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }
}
