package ch.zhaw.ch.visualization;

import com.jjoe64.graphview.GraphView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ch.zhaw.ch.TrackInfo;
import ch.zhaw.ch.fft.FFT;

public abstract class Visualizer {
    public abstract void init(TrackInfo info);

    public abstract void setFFT(FFT fft);

    public abstract void displaySamples(float[] samples);

    public abstract void setGraph(GraphView graph);

    public static Visualizer getNewInstance(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<Visualizer> clazz = (Class<Visualizer>) Class.forName(classname);
        Constructor<Visualizer> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }
}
