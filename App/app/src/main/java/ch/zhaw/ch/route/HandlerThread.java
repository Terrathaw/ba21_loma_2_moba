package ch.zhaw.ch.route;

import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;

/***
 * Provides threading functionalities to all classes that inherit from the Handler interface.
 */

public class HandlerThread extends Thread {
    private final String TAG;
    private Handler handler;

    private volatile boolean running = false;
    private volatile boolean init = false;
    private volatile boolean paused = false;

    public HandlerThread(String tag, Handler handler) {
        this.TAG = tag;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            while (running) {
                try {
                    handler.init();
                } catch (Exception e) {
                    throw new IllegalStateException("unable to initialize handler", e);
                }
                init = false;
                while (!init && running) {
                    if (paused || !handler.ready()) sleep(10);
                    else handler.process();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                handler.close();
            } catch (IOException e) {
                Log.e(TAG, ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public void initializeHandler() {
        init = true;
        startHandler();
    }

    public void startHandler() {
        running = true;
        paused = false;
    }

    public void stopHandler() {
        running = false;
    }

    public void pauseHandler() {
        paused = true;
        handler.pause();
    }

    public void resumeHandler() {
        paused = false;
        handler.resume();
    }
}
