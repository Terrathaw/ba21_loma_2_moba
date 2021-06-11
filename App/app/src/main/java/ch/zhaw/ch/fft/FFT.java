package ch.zhaw.ch.fft;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ch.zhaw.ch.dsp.ComplexFrame;

/***
 * Interface that provides functionalities to process samples with a fft.
 */

public interface FFT {

    void init(int resolution);

    ComplexFrame forward(float[] frame);

    float[] inverse(ComplexFrame frame);


    static FFT getNewInstance(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<FFT> clazz = (Class<FFT>) Class.forName(classname);
        Constructor<FFT> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }
}
