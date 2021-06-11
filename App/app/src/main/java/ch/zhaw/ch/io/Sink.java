package ch.zhaw.ch.io;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ch.zhaw.ch.SourceInfo;

/***
 * Interface that provides functionalities to write and create music file formats.
 */

public interface Sink {
    void open(String path, String filename) throws IOException;

    boolean isInitialized();

    void writeHeader(SourceInfo header) throws IOException;

    void write(float[] buffer) throws IOException;

    void close() throws IOException;

    static Sink getNewInstance(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<Sink> clazz = (Class<Sink>) Class.forName(classname);
        Constructor<Sink> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }
}
