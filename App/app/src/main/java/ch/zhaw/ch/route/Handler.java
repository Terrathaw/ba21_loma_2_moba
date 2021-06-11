package ch.zhaw.ch.route;

import java.io.IOException;

/***
 * Interface for all handler classes that should be able to use threading. In this context will be used with the helper class HandlerThreads to provide the necessary threading functionalities.
 */

public interface Handler {

    boolean init() throws Exception;

    boolean ready();

    void process() throws IOException;

    void close() throws IOException;

    void pause();

    void resume();
}
