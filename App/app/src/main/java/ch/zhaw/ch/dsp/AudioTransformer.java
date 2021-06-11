package ch.zhaw.ch.dsp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ch.zhaw.ch.ModuleInfo;
import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.TrackInfo;

/***
 *
 */

public interface AudioTransformer {

    void init(ModuleInfo moduleInfo, TrackInfo trackInfo, SourceInfo sourceInfo) throws Exception;

    float[] transformBuffered(float[] samples);

    float[] transformFrame(float[] frame);

    float[] test(float[] samples);


    static AudioTransformer getNewInstance(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<AudioTransformer> clazz = (Class<AudioTransformer>) Class.forName(classname);
        Constructor<AudioTransformer> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }
}
