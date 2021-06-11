package ch.zhaw.ch.util;

import java.util.Collections;
import java.util.LinkedList;

public class MovingMedian {
    private LinkedList<Float> heap;
    private int index;
    private int size;

    public MovingMedian(int size, float percentile){
        this.size = size;
        getPercentileIndex(percentile);
        heap = new LinkedList<>();
        for(int i = 0; i < size; i++)
            heap.add(0f);
    }
    private void getPercentileIndex(float percentile){
        index = (int) (size * percentile / 100);
        if(index >= size){
            index = size-1;
        }
        if(index < 0){
            index = 0;
        }
    }
    public void Put(float number){
        heap.pop();
        heap.add(number);
    }

    public float Get(){
        LinkedList<Float> heapCopy = heap;
        Collections.sort(heapCopy);
        return heapCopy.get(index);
    }
}
