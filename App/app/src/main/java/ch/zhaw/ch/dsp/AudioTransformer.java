package ch.zhaw.ch.dsp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ch.zhaw.ch.ModuleInfo;
import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.TrackInfo;

/***
 * Interface for different kind of audio transformer (e.g. RubberBand, PitchShifter).
 */

public interface AudioTransformer {

    /***
     * Initializes the class with the passed parameters. Can be called multiple times during runtime to change
     * all parameters (e.g. pitch shifters, FFTs, resampler).
     * @param moduleInfo
     * @param trackInfo
     * @param sourceInfo
     * @throws Exception
     */
    void init(ModuleInfo moduleInfo, TrackInfo trackInfo, SourceInfo sourceInfo) throws Exception;

    /***
     * Transforms a whole sample buffer.
     * @param samples
     * @return transformed sample buffer
     */
    float[] transformBuffered(float[] samples);

    /***
     * Transforms a single frame.
     * @param frame
     * @return transformed frame.
     */
    float[] transformFrame(float[] frame);

    float[] test(float[] samples);

    /***
     * Creates an instance of a class inheriting from this AudioTransformer
     * @param classname name
     * @return instance inheriting from AudioTransformer
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    static AudioTransformer getNewInstance(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<AudioTransformer> clazz = (Class<AudioTransformer>) Class.forName(classname);
        Constructor<AudioTransformer> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }
}
