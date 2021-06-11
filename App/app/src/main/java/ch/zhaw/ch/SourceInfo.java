package ch.zhaw.ch;


import android.media.AudioFormat;
import android.util.Log;

/***
 * http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
 * Contains properties of the source file. The Properties are read from the Wave file header and inserted into this class. These informations are need by our Vocoder and for the storage of new files.
 */
public class SourceInfo {
    private static final String TAG = SourceInfo.class.getSimpleName();

    private String name;

    private short audioFormat;
    private short numberOfChannels;
    private int sampleRate;
    private int byteRate;
    private short blockAlign;
    private short bitDepth;
    private int dataSize;

    private int channelConfig;
    private int byteBufferSize;

    public SourceInfo() {
        sampleRate = 22050;
        bitDepth = 16;
        numberOfChannels = 1;
        init();
    }

    public void init() {
        byteRate = sampleRate * numberOfChannels * bitDepth / 8;
        blockAlign = (short) (numberOfChannels * bitDepth / 8);
        audioFormat = 1; //WAVE_FORMAT_PCM
        channelConfig = getChanelConfig(numberOfChannels);
    }

    public void log() {
        Log.v(TAG, "numberOfChannels: \t" + numberOfChannels);
        Log.v(TAG, "sampleRate: \t" + sampleRate);
        Log.v(TAG, "dataRate: \t" + byteRate);
        Log.v(TAG, "dataBlockSize: \t" + blockAlign);
        Log.v(TAG, "bitDepth: \t" + bitDepth);
        Log.v(TAG, "channelConfig: \t" + channelConfig);
        Log.v(TAG, "bufferSize: \t" + byteBufferSize);
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

    public String getName() {
        return name;
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

    public int getDataSize() {
        return dataSize;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public int getByteBufferSize() {
        return byteBufferSize;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAudioFormat(short audioFormat) {
        this.audioFormat = audioFormat;
    }

    public void setNumberOfChannels(short numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setByteRate(int byteRate) {
        this.byteRate = byteRate;
    }

    public void setBlockAlign(short blockAlign) {
        this.blockAlign = blockAlign;
    }

    public void setBitDepth(short bitDepth) {
        this.bitDepth = bitDepth;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public void setByteBufferSize(int byteBufferSize) {
        this.byteBufferSize = byteBufferSize;
    }

    public int getSampleBufferSize() {
        return byteBufferSize / 2;
    }

}