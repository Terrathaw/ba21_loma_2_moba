package ch.zhaw.ch.io;


import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/***
 * http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
 */
public class WaveHeader {
    private static final String TAG = WaveHeader.class.getSimpleName();

    public static final int WAV_CHUNKSIZE_EXCLUDE_DATA = 36;
    public static final int WAV_CHUNKSIZE_OFFSET = 4;
    public static final int WAV_SUB_CHUNKSIZE1_OFFSET = 16;
    public static final int WAV_SUB_CHUNKSIZE2_OFFSET = 40;
    public static final int MAX_HEADER_SIZE = 68;
    public static final int FORMAT_CHUNK_SIZE_OFFSET = 16;
    private static final int MIN_BUFFER_SIZE = 8192;

    private String chunkID = "RIFF";
    private int chunkSize = 4;
    private String format = "WAVE";

    private String subChunk1ID = "fmt ";
    private int subChunk1Size = 16;
    private short audioFormat;
    private short numberOfChannels;
    private int sampleRate;
    private int byteRate;
    private short blockAlign;
    private short bitDepth;

    private String subChunk2ID = "data";
    private int subChunk2Size = 0;

    private WaveFormat waveFormat;
    private int channelConfig;
    private int byteBufferSize;

    public boolean setMetadata(int sampleRateInHz, int channels, int bitsPerSample) {
        try {
            sampleRate = sampleRateInHz;
            bitDepth = (short) bitsPerSample;
            numberOfChannels = (short) channels;
            byteRate = sampleRate * numberOfChannels * bitDepth / 8;
            blockAlign = (short) (numberOfChannels * bitDepth / 8);
            audioFormat = 1; //WAVE_FORMAT_PCM

            init();
        } catch (Exception e) {
            Log.e(TAG, "An error occurred while interpreting tha wave metadata:");
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    public boolean tryReadMetadata(InputStream inputStream) {
        try {
            byte[] intValue = new byte[4];
            inputStream.skip(FORMAT_CHUNK_SIZE_OFFSET);

            inputStream.read(intValue);
            int fmtSize = ByteBuffer.wrap(intValue).order(ByteOrder.LITTLE_ENDIAN).getInt();
            if (fmtSize < 16 || fmtSize > 40)
                throw new IllegalArgumentException("fmt size not in in range 16 - 40. fmt size: " + fmtSize);

            byte[] formatData = new byte[fmtSize];
            inputStream.read(formatData);

            ByteBuffer formatBuffer = ByteBuffer.wrap(formatData).order(ByteOrder.LITTLE_ENDIAN);
            audioFormat = formatBuffer.getShort();
            numberOfChannels = formatBuffer.getShort();
            sampleRate = formatBuffer.getInt();
            byteRate = formatBuffer.getInt();
            blockAlign = formatBuffer.getShort();
            bitDepth = formatBuffer.getShort();

            forwardToDataChunk(inputStream);

            inputStream.read(intValue);
            subChunk2Size = ByteBuffer.wrap(intValue).getInt();

            init();
        } catch (Exception e) {
            Log.e(TAG, "An error occurred while interpreting tha wave metadata:");
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    public void log() {
        Log.v(TAG, "format: \t" + format);
        Log.v(TAG, "numberOfChannels: \t" + numberOfChannels);
        Log.v(TAG, "sampleRate: \t" + sampleRate);
        Log.v(TAG, "dataRate: \t" + byteRate);
        Log.v(TAG, "dataBlockSize: \t" + blockAlign);
        Log.v(TAG, "bitDepth: \t" + bitDepth);
        Log.v(TAG, "dataChunkSize: \t" + subChunk2Size);
        Log.v(TAG, "waveFormat: \t" + waveFormat);
        Log.v(TAG, "channelConfig: \t" + channelConfig);
        Log.v(TAG, "bufferSize: \t" + byteBufferSize);
    }

    private void init() {
        waveFormat = WaveFormat.parse(audioFormat);
        channelConfig = getChanelConfig(numberOfChannels);
        byteBufferSize = Math.max(MIN_BUFFER_SIZE, AudioTrack.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT));
    }


    private void forwardToDataChunk(InputStream inputStream) throws IOException {
        byte[] prev = new byte[2];
        byte[] curr = new byte[2];
        String data = "";
        while (!subChunk2ID.equals(data)) {
            System.arraycopy(curr, 0, prev, 0, curr.length);
            if (inputStream.read(curr) <= 0)
                throw new IllegalArgumentException(String.format("unable to find data chunk with id %s", subChunk2ID));
            data = new String(prev, StandardCharsets.UTF_8) + new String(curr, StandardCharsets.UTF_8);
        }
    }

    private int getChanelConfig(int numberOfChannels) {

        switch (numberOfChannels) {
            case 1:
                return AudioFormat.CHANNEL_OUT_MONO;
            case 2:
                return AudioFormat.CHANNEL_OUT_STEREO;
            default:
                throw new UnsupportedOperationException(String.format("Android doesn't support more than 2 audio channels. Current number of channels: %d", numberOfChannels));
        }
    }

    public String getChunkID() {
        return chunkID;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public String getFormat() {
        return format;
    }

    public String getSubChunk1ID() {
        return subChunk1ID;
    }

    public int getSubChunk1Size() {
        return subChunk1Size;
    }

    public short getAudioFormat() {
        return audioFormat;
    }

    public short getNumberOfChannels() {
        return numberOfChannels;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getByteRate() {
        return byteRate;
    }

    public short getBlockAlign() {
        return blockAlign;
    }

    public short getBitDepth() {
        return bitDepth;
    }

    public String getSubChunk2ID() {
        return subChunk2ID;
    }

    public int getSubChunk2Size() {
        return subChunk2Size;
    }

    public WaveFormat getWaveFormat() {
        return waveFormat;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public int getByteBufferSize() {
        return byteBufferSize;
    }

    public int getSampleBufferSize() {
        return byteBufferSize / 2;
    }
}