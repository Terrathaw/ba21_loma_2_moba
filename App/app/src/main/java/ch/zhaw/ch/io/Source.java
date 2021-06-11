package ch.zhaw.ch.io;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ch.zhaw.ch.SourceInfo;

/***
 * Interface that provides functionalities to read and understand music file formats.
 */

public interface Source {

    void init(InputStream inputStream);

    void setSourceInfo(SourceInfo sourceInfo) throws IOException;

    int read(float[] buffer) throws IOException;

    void close() throws IOException;

    static Source getNewInstance(String classname) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<Source> clazz = (Class<Source>) Class.forName(classname);
        Constructor<Source> ctor = clazz.getConstructor();
        return ctor.newInstance();
    }
}
