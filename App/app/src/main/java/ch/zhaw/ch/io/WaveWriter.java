package ch.zhaw.ch.io;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.util.PCMUtil;

/***
 * Provides functionalities to write samples into a wave file.
 */

public class WaveWriter implements Sink {
    private static final String TAG = WaveWriter.class.getName();
    private static final String CHUNK_ID = "RIFF";
    private static final int CHUNK_SIZE = 4;
    private static final String FORMAT = "WAVE";
    private static final String SUB_CHUNK_1_ID = "fmt ";
    private static final int SUB_CHUNK_1_SIZE = 16;
    private static final String SUB_CHUNK_2_ID = "data";

    private String filePath;
    private int dataSize = 0;
    private DataOutputStream outputStream;

    public void open(String path, String fileName) throws IOException {
        if (outputStream != null) close();

        File directory = new File(path);
        if (!directory.exists() && !directory.mkdir()) {
            throw new IOException(String.format("failed to create directory %s", path));
        }
        filePath = String.format("%s/%s.wav", path, fileName);
        Log.v(TAG, filePath);
        dataSize = 0;
        try {
            outputStream = new DataOutputStream(new FileOutputStream(filePath));
        } catch (Exception e) {
            throw new IOException(String.format("failed to open file %s", filePath), e);
        }
    }

    public void close() throws IOException {
        if (!writeDataSize())
            throw new IllegalStateException("wav writer failed to write data chunk size");
        outputStream.flush();
        outputStream.close();
        outputStream = null;
    }

    public void write(float[] buffer) throws IOException {
        if (outputStream == null) {
            throw new IllegalStateException("output stream not initialized");
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length * 2).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < buffer.length; i++) {
            byteBuffer.putShort(PCMUtil.float2Short(buffer[i]));
        }
        outputStream.write(byteBuffer.array());
        dataSize += buffer.length * 2;
    }

    public void writeHeader(byte[] header) throws IOException {
        outputStream.write(header, 0, header.length);
    }

    public void writeHeader(SourceInfo header) throws IOException {
        if (outputStream == null) {
            throw new IllegalStateException("output stream not initialized");
        }

        outputStream.writeBytes(CHUNK_ID);
        outputStream.write(intToByteArray(CHUNK_SIZE), 0, 4);
        outputStream.writeBytes(FORMAT);
        outputStream.writeBytes(SUB_CHUNK_1_ID);
        outputStream.write(intToByteArray(SUB_CHUNK_1_SIZE), 0, 4);
        outputStream.write(shortToByteArray(header.getAudioFormat()), 0, 2);
        outputStream.write(shortToByteArray(header.getNumberOfChannels()), 0, 2);
        outputStream.write(intToByteArray(header.getSampleRate()), 0, 4);
        outputStream.write(intToByteArray(header.getByteRate()), 0, 4);
        outputStream.write(shortToByteArray(header.getBlockAlign()), 0, 2);
        outputStream.write(shortToByteArray(header.getBitDepth()), 0, 2);
        outputStream.writeBytes(SUB_CHUNK_2_ID);
        outputStream.write(intToByteArray(dataSize), 0, 4);
    }

    public boolean isInitialized() {
        return outputStream != null;
    }

    private boolean writeDataSize() {
        if (outputStream == null) {
            return false;
        }

        try {
            RandomAccessFile wavFile = new RandomAccessFile(filePath, "rw");
            wavFile.seek(WaveReader.WAV_CHUNKSIZE_OFFSET);
            wavFile.write(intToByteArray((dataSize + WaveReader.WAV_CHUNKSIZE_EXCLUDE_DATA)), 0, 4);
            wavFile.seek(WaveReader.WAV_SUB_CHUNKSIZE2_OFFSET);
            wavFile.write(intToByteArray((dataSize)), 0, 4);
            wavFile.close();
            Log.d(TAG, String.format("data size: %d", dataSize));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static byte[] intToByteArray(int data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
    }

    private static byte[] shortToByteArray(short data) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array();
    }
}
