package ch.zhaw.ch.util;

import org.apache.commons.collections4.queue.CircularFifoQueue;

public class AverageQueue {
    private CircularFifoQueue<Float> queue;
    private float sum;
    private float avg;
    private boolean recalcRequired;
    private float size;

    public AverageQueue(int size){
        queue = new CircularFifoQueue<>(size);
        for (int i = 0; i < size; i++) {
            queue.add(0f);
        }
        this.size = size;
        sum = 0;
        avg = 0;
        recalcRequired = true;
    }

    public void push(float value){
        recalcRequired = true;
        sum += value;
        sum -= queue.poll();
        queue.add(value);
    }

    public float getAvg(){
        if(recalcRequired){
            avg = sum / size;
            recalcRequired = false;
        }
        return avg;
    }
}
