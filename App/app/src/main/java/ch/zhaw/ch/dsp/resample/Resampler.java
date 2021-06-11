package ch.zhaw.ch.dsp.resample;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/***
 * Interface for resamplers.
 */
public interface Resampler {
    /***
     * Initializes the resampler.
     * @param frameSize
     * @param stretchFactor
     */
    void init(int frameSize, float stretchFactor);

    /***
     * returns the calculated frame size used by the resampler
     * @return resampler frame size
     */
    int getResampleFrameSize();

    /***
     * Resamples a frame into the resampledFrame
     * @param frame
     * @param resampledFrame
     */
    void resample(float[] frame, float[] resampledFrame);

    /***
     * Creates an instance of a class inheriting from this Resampler
     * @param classname
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    static Resampler getPhaseShifter(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<Resampler> clazz = (Class<Resampler>) Class.forName(classname);
        Constructor<Resampler> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }
}
