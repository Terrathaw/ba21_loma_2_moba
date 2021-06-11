package ch.zhaw.ch.dsp.resample;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface Resampler {
    void init(int frameSize, float stretchFactor);

    int getResampleFrameSize();

    void resample(float[] frame, float[] resampledFrame);

    static Resampler getPhaseShifter(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<Resampler> clazz = (Class<Resampler>) Class.forName(classname);
        Constructor<Resampler> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }
}
