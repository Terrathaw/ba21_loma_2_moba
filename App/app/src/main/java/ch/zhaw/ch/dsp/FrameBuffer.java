package ch.zhaw.ch.dsp;

import java.nio.BufferOverflowException;

import ch.zhaw.ch.util.ArrayUtil;

/***
 * This class prevents the garbage collector and instancing from dropping the realtime performance
 * of our app, by storing frame data into reusable buffers.
 */

public class FrameBuffer {

    private float[] buffer;
    private int bufferSize;
    private int readIndex;
    private int writeIndex;

    /***
     * Constucts a frame buffer with the given parameters.
     * @param bufferSize
     * @param writeIndex
     */
    public FrameBuffer(int bufferSize, int writeIndex) {
        this.bufferSize = bufferSize;
        this.buffer = ArrayUtil.rangeOfValue(bufferSize, (float) 0);
        this.writeIndex = writeIndex;
    }

    /***
     * Writes a frame into the buffer. also Provides functionalities for OLA
     * @param writeBuffer
     * @param length
     * @param stepSize
     * @param overlapAdd
     */
    public void write(float[] writeBuffer, int length, int stepSize, boolean overlapAdd) {
        if (Math.signum(writeIndex + length) < 0) writeIndex = getWrappedWriteIndex(writeIndex);

        for (int i = 0; i < length; i++) {
            throwOnOverflow(writeIndex + i, readIndex);
            int wrappedWriteIdex = getWrappedWriteIndex(writeIndex + i);
            buffer[wrappedWriteIdex] = overlapAdd ? buffer[wrappedWriteIdex] + writeBuffer[i] : writeBuffer[i];
        }

        writeIndex += stepSize;
    }

    /***
     * Reads data from the buffer. Adds functionality to clear a buffer by setting the clear boolean.
     * @param length
     * @param stepSize
     * @param clear
     * @return
     */
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

    /***
     * Returns the current size of the readable buffer part
     * @return
     */
    public int size() {
        return writeIndex - readIndex;
    }

    /***
     * throws a buffer overflow.
     * @param writeIndex
     * @param readIndex
     */
    private void throwOnOverflow(int writeIndex, int readIndex) {
        if (writeIndex - readIndex == bufferSize || writeIndex < readIndex) {
            throw new BufferOverflowException();
        }
    }

    private int getWrappedWriteIndex(int index) {
        return index % bufferSize;
    }


}
