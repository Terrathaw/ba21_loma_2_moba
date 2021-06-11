package ch.zhaw.ch.route;

import java.io.IOException;
import java.util.ArrayList;

/***
 * This class runs all the other route handlers in one thread. Mainly for testing and debugging  purposes.
 */

public class AllInOneHandler implements Handler {


    private SourceHandler sourceHandler;
    private RouteHandler routeHandler;
    private SinkHandler sinkHandler;
    private ArrayList<Handler> handlers;
    private SyncedCycleBuffer inputBuffer;
    private SyncedCycleBuffer outputBuffer;

    public AllInOneHandler(SourceHandler sourceHandler, RouteHandler routeHandler, SinkHandler sinkHandler, SyncedCycleBuffer inputBuffer, SyncedCycleBuffer outputBuffer) {
        this.sourceHandler = sourceHandler;
        this.routeHandler = routeHandler;
        this.sinkHandler = sinkHandler;
        this.handlers = new ArrayList<>();
        handlers.add(sourceHandler);
        handlers.add(routeHandler);
        handlers.add(sinkHandler);
        this.inputBuffer = inputBuffer;
        this.outputBuffer = outputBuffer;
    }

    @Override
    public boolean init() throws Exception {
        for (Handler handler : handlers) {
            handler.init();
        }
        return true;
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public void process() throws IOException {
        inputBuffer.tryLock();
        sourceHandler.process();
        inputBuffer.tryLock();
        routeHandler.process();
        outputBuffer.tryLock();
        routeHandler.process();
        outputBuffer.tryLock();
        sinkHandler.process();
    }

    @Override
    public void close() throws IOException {
        for (Handler h : handlers) {
            h.close();
        }
    }

    @Override
    public void pause() {
        handlers.forEach(h -> h.pause());

    }

    @Override
    public void resume() {
        handlers.forEach(h -> h.resume());

    }
}
