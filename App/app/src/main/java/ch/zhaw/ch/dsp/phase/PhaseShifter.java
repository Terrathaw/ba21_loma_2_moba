package ch.zhaw.ch.dsp.phase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ch.zhaw.ch.dsp.ComplexFrame;

public interface PhaseShifter {

    /***
     * Initializes the phase shifter.
     * @param frameSize
     * @param frameSizeNyquist
     * @param hopSizeAnalysis
     * @param strechFactor
     * @param sampleRate
     * @param transientDetectionType
     * @param phaseResetType
     */
    void init(int frameSize, int frameSizeNyquist, int hopSizeAnalysis, float strechFactor, int sampleRate, TransientDetectionType transientDetectionType, PhaseResetType phaseResetType);

    /***
     * Shifts a frame with the parameters set up in init()
     * @param frame
     * @return pitch shifted frame
     */
    float[] shift(ComplexFrame frame);

    /***
     * Creates an instance of a class inheriting from this PhaseShifter
      * @param classname
     * @return instance inheriting from PhaseShifter
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    static PhaseShifter getPhaseShifter(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<PhaseShifter> clazz = (Class<PhaseShifter>) Class.forName(classname);
        Constructor<PhaseShifter> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }
}
