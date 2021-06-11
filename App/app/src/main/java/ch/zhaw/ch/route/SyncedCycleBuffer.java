package ch.zhaw.ch.route;

import android.util.Log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/***
 * This is a helper class that provides access to a threadsafe cycle buffer.
 * TODO: make it cycle instead of adding.
 */
public class SyncedCycleBuffer {
    public static final int SYNCED_CYCLE_BUFFER_SIZE = 3;
    private String TAG;

    private float[][] bufferCycle;
    private ReentrantLock lock = new ReentrantLock();
    int cycleSize;
    int currentReadPosition;
    int lapCount;
    int currentWritePosition;

    public SyncedCycleBuffer(String TAG) {
        this.TAG = TAG;
    }

    public boolean init(int cycleSize, int buffersize) {
        if (!waitForLock()) return false;
        this.cycleSize = cycleSize;
        bufferCycle = new float[cycleSize][buffersize];
        currentReadPosition = 0;
        currentWritePosition = 0;
        lock.unlock();
        return true;
    }

    public boolean tryLock() {
        //Log.v(TAG, "lock buffer");
        return lock.tryLock();
    }

    public boolean waitForLock() {
        while (!tryLock()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted while waiting for lock");
                return false;
            }
        }
        return true;
    }

    public void writeNextBuffer(float[] buffer) {
        //Log.v(TAG, "buffer stored");
        bufferCycle[currentWritePosition] = Arrays.copyOf(buffer, buffer.length);
        incrementWritePosition();
        lock.unlock();
    }

    private void incrementWritePosition(){
        if(currentWritePosition < cycleSize-1){
            currentWritePosition++;
        }else{
            currentWritePosition = 0;
            lapCount++;
        }
    }

    private void incrementReadPosition(){
        if(currentReadPosition < cycleSize-1){
            currentReadPosition++;
        }else{
            currentReadPosition = 0;
            lapCount--;
        }
    }

    public boolean checkIfBufferAvailable() {
        return lapCount==1 || currentReadPosition < currentWritePosition;
    }

    public float[] getNextBuffer() {
        float[] buffer = Arrays.copyOf(bufferCycle[currentReadPosition], bufferCycle[currentReadPosition].length);
        incrementReadPosition();
        lock.unlock();
        return buffer;

    }

    public boolean hasAvailableSpace() {
        return lapCount==0 && currentReadPosition == currentWritePosition;
    }
}
