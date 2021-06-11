package ch.zhaw.ch.io;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import ch.zhaw.ch.Config;
import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.util.PCMUtil;


/***
 * Provides functionalities to read samples from a wave file.
 */


public class WaveReader implements Source {
    private static final String TAG = WaveReader.class.getSimpleName();

    public static final int WAV_CHUNKSIZE_EXCLUDE_DATA = 36;
    public static final int WAV_CHUNKSIZE_OFFSET = 4;
    public static final int WAV_SUB_CHUNKSIZE1_OFFSET = 16;
    public static final int WAV_SUB_CHUNKSIZE2_OFFSET = 40;
    public static final int MAX_HEADER_SIZE = 68;
    public static final int FORMAT_CHUNK_SIZE_OFFSET = 16;
    private static final int MIN_BUFFER_SIZE = 8192;
    private static final String SUB_CHUNK_2_ID = "data";
    private static final String SUB_CHUNK_1_ID = "fmt ";

    private DataInputStream inputStream;
    private SourceInfo sourceInfo;
    private int originalNumberOfChannels;

    public void openFile(String filepath) throws IOException {
        init(new FileInputStream(filepath));
    }

    public void init(InputStream inputStream) {
        if (inputStream != null) {
            try {
                close();
            } catch (Exception e) {
                Log.e(TAG, "input stream could not be closed and will be abandoned");
                Log.e(TAG, ExceptionUtils.getStackTrace(e));
            }
        }
        this.inputStream = new DataInputStream(inputStream);
    }

    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
    }

    public SourceInfo getHeader() {
        return sourceInfo;
    }

    public byte[] getByteHeader() throws IOException{
        int headerSize = 40;
        byte[] header = new byte[headerSize];
        inputStream.read(header);
        return header;
    }

    public int read(float[] buffer) throws IOException {
        if (inputStream == null || sourceInfo == null) {
            throw new IllegalStateException("input stream or source info not initialized");
        }

        byte[] bytes = new byte[buffer.length * 2 * originalNumberOfChannels];
//        byte[] bytes = new byte[buffer.length * 2];
        int byteCount = inputStream.read(bytes, 0, bytes.length);
        if (byteCount == -1) {
            Log.d(TAG, "end of input stream reached");
            return 0;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = 0;
            for (int j = 0; j < originalNumberOfChannels; j++) {
                buffer[i] += PCMUtil.short2Float(byteBuffer.getShort()) / originalNumberOfChannels;
            }
//            buffer[i] = PCMUtil.short2Float(byteBuffer.getShort()) / sourceInfo.getNumberOfChannels();
        }
        return byteCount;
    }

    public void setSourceInfo(SourceInfo info) throws IOException {
        if (inputStream == null) {
            throw new IllegalStateException("input stream is no initialized");
        }

        this.sourceInfo = info;
        byte[] intValue = new byte[4];

        /**check if truly wav with riff header*/

        forwardToChunkID(inputStream, SUB_CHUNK_1_ID);

        inputStream.read(intValue);
        int fmtSize = ByteBuffer.wrap(intValue).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (fmtSize < 16 || fmtSize > 40)
            throw new IllegalArgumentException("fmt size not in in range 16 - 40. fmt size: " + fmtSize);

        byte[] formatData = new byte[fmtSize];
        inputStream.read(formatData);

        ByteBuffer formatBuffer = ByteBuffer.wrap(formatData).order(ByteOrder.LITTLE_ENDIAN);
        sourceInfo.setAudioFormat(formatBuffer.getShort());
        sourceInfo.setNumberOfChannels(formatBuffer.getShort());
        sourceInfo.setSampleRate(formatBuffer.getInt());
        sourceInfo.setByteRate(formatBuffer.getInt());
        sourceInfo.setBlockAlign(formatBuffer.getShort());
        sourceInfo.setBitDepth(formatBuffer.getShort());

        originalNumberOfChannels = sourceInfo.getNumberOfChannels();
        sourceInfo.setNumberOfChannels((short) 1);

        forwardToChunkID(inputStream, SUB_CHUNK_2_ID);
        inputStream.read(intValue);
        sourceInfo.setDataSize(ByteBuffer.wrap(intValue).getInt());
        sourceInfo.init();
        Config.getInstance().setByteBufferSize(AudioTrack.getMinBufferSize(sourceInfo.getSampleRate(), sourceInfo.getChannelConfig(), AudioFormat.ENCODING_PCM_16BIT));
    }


    public int setSourceInfo(SourceInfo info, byte[] header) throws IOException {
        if (inputStream == null) {
            throw new IllegalStateException("input stream is no initialized");
        }

        ByteBuffer headerData = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);

        this.sourceInfo = info;
        byte[] intValue = new byte[4];
        headerData.position(FORMAT_CHUNK_SIZE_OFFSET);

        int fmtSize =  headerData.getInt();
        if (fmtSize < 16 || fmtSize > 40)
            throw new IllegalArgumentException("fmt size not in in range 16 - 40. fmt size: " + fmtSize);

        byte[] formatData = new byte[fmtSize];
        inputStream.read(formatData);

        sourceInfo.setAudioFormat(headerData.getShort());
        sourceInfo.setNumberOfChannels(headerData.getShort());
        sourceInfo.setSampleRate(headerData.getInt());
        sourceInfo.setByteRate(headerData.getInt());
        sourceInfo.setBlockAlign(headerData.getShort());
        sourceInfo.setBitDepth(headerData.getShort());

        originalNumberOfChannels = sourceInfo.getNumberOfChannels();
        sourceInfo.setNumberOfChannels((short) 1);

        forwardToChunkID(inputStream, SUB_CHUNK_2_ID);
        inputStream.read(intValue);
        sourceInfo.setDataSize(ByteBuffer.wrap(intValue).getInt());
        sourceInfo.init();
        return sourceInfo.getSampleRate()*sourceInfo.getChannelConfig() * AudioFormat.ENCODING_PCM_16BIT;
    }

    private void forwardToChunkID(InputStream inputStream, String chunkID) throws IOException {
        byte[] prev = new byte[2];
        byte[] curr = new byte[2];
        String data = "";
        while (!chunkID.equals(data)) {
            System.arraycopy(curr, 0, prev, 0, curr.length);
            if (inputStream.read(curr) <= 0)
                throw new IllegalArgumentException(String.format("unable to find data chunk with id %s", SUB_CHUNK_2_ID));
            data = new String(prev, StandardCharsets.UTF_8) + new String(curr, StandardCharsets.UTF_8);
        }
    }

}
