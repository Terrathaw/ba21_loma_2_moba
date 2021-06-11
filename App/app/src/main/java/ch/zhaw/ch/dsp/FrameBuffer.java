package ch.zhaw.ch.dsp;

import java.nio.BufferOverflowException;

import ch.zhaw.ch.util.ArrayUtil;

public class FrameBuffer {

    private float[] buffer;
    private int bufferSize;
    private int readIndex;
    private int writeIndex;

    public FrameBuffer(int bufferSize, int writeIndex) {
        this.bufferSize = bufferSize;
        this.buffer = ArrayUtil.rangeOfValue(bufferSize, (float) 0);
        this.writeIndex = writeIndex;
    }

    public void write(float[] writeBuffer, int length, int stepSize, boolean overlapAdd) {
        if (Math.signum(writeIndex + length) < 0) writeIndex = getWrappedWriteIndex(writeIndex);

        for (int i = 0; i < length; i++) {
            throwOnOverflow(writeIndex + i, readIndex);
            int wrappedWriteIdex = getWrappedWriteIndex(writeIndex + i);
            buffer[wrappedWriteIdex] = overlapAdd ? buffer[wrappedWriteIdex] + writeBuffer[i] : writeBuffer[i];
        }

        writeIndex += stepSize;
    }

    public float[] read(int length, int stepSize, boolean clear) {
        if (Math.signum(readIndex + length) < 0) readIndex = getWrappedWriteIndex(readIndex);
        float[] readBuffer = new float[length];

        for (int i = 0; i < length; i++) {
            throwOnOverflow(writeIndex, readIndex + i);
            int wrappedReadIndex = getWrappedWriteIndex(readIndex + i);
            readBuffer[i] = buffer[wrappedReadIndex];
        }

        if (clear) {
            for (int i = 0; i < stepSize; i++) {
                int wrappedReadIndex = getWrappedWriteIndex(readIndex + i);
                buffer[wrappedReadIndex] = 0;
            }
        }
        readIndex += stepSize;
        return readBuffer;
    }

    public int size() {
        return writeIndex - readIndex;
    }

    private void throwOnOverflow(int writeIndex, int readIndex) {
        if (writeIndex - readIndex == bufferSize || writeIndex < readIndex) {
            throw new BufferOverflowException();
        }
    }


    private int getWrappedWriteIndex(int index) {
        return index % bufferSize;
    }


}
