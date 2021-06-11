package ch.zhaw.ch.java.audio;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.TrackInfo;
import ch.zhaw.ch.util.PCMUtil;

public class AudioFileUtil {
    public static final String resOutRoot = "src\\test\\res\\audio\\out\\";
    public static final String resInRoot = "src\\test\\res\\audio\\in\\";

    public static final int WAV_CHUNKSIZE_EXCLUDE_DATA = 36;
    public static final int WAV_CHUNKSIZE_OFFSET = 4;
    public static final int WAV_SUB_CHUNKSIZE1_OFFSET = 16;
    public static final int WAV_SUB_CHUNKSIZE2_OFFSET = 40;
    public static final int MAX_HEADER_SIZE = 68;
    public static final int FORMAT_CHUNK_SIZE_OFFSET = 16;
    private static final int MIN_BUFFER_SIZE = 8192;
    private static final String SUB_CHUNK_2_ID = "data";

    public static void writeTestResult(TrackInfo trackInfo, SourceInfo sourceInfo, String name, float[] base, float[] reference, float[] transformed) {
        String path = resOutRoot + name;
        File dir = new File(path);
        if (!dir.exists())
            if (!dir.mkdir())
                throw new IllegalArgumentException(String.format("could not create dir %s", path));

        writeWav(path, String.format("%s_%+d", "base", trackInfo.getHalfToneStepsToShift()), base, sourceInfo.getSampleRate());
        writeWav(path, String.format("%s_%+d", "reference", trackInfo.getHalfToneStepsToShift()), reference, sourceInfo.getSampleRate());
        writeWav(path, String.format("%s_%+d", "transformed", trackInfo.getHalfToneStepsToShift()), transformed, sourceInfo.getSampleRate());
    }

    public static boolean writeWav(String fileName, float[] samples, int sampleRate) {
        return writeWav(resOutRoot, fileName, samples, sampleRate);
    }

    public static boolean writeWav(String path, String fileName, float[] samples, int sampleRate) {

        try {
            final byte[] byteBuffer = new byte[samples.length * 2];
            short[] samplesShort = PCMUtil.float2ShortArray(samples);

            int bufferIndex = 0;
            for (int i = 0; i < byteBuffer.length; i++) {
                final int x = (int) (samplesShort[bufferIndex++]);

                byteBuffer[i++] = (byte) x;
                byteBuffer[i] = (byte) (x >>> 8);
            }

            File out = new File(String.format("%s\\%s.wav", path, fileName));

            final boolean bigEndian = false;
            final boolean signed = true;

            final int bits = 16;
            final int channels = 1;

            AudioFormat format = new AudioFormat((float) sampleRate, bits, channels, signed, bigEndian);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, samples.length);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
            audioInputStream.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static float[] readWav(String fileName, SourceInfo sourceInfo) {
        return readWav(resInRoot, fileName, sourceInfo);
    }

    public static float[] readWav(String path, String fileName, SourceInfo sourceInfo) {
        String filePath = String.format("%s\\%s.wav", path, fileName);
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(filePath));
            fillSourceInfo(dataInputStream, sourceInfo);
            float[] sampleBuffer = new float[sourceInfo.getSampleRate()];
            ArrayList<Float> samples = new ArrayList<>();
            int readCount;
            while ((readCount = read(dataInputStream, sourceInfo, sampleBuffer)) > 0) {
                for (int i = 0; i < readCount; i++) {
                    samples.add(sampleBuffer[i]);
                }
            }

            float[] temp = new float[samples.size()];
            for (int i = 0; i < samples.size(); i++) {
                temp[i] = samples.get(i);
            }
            return temp;

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static int read(DataInputStream inputStream, SourceInfo sourceInfo, float[] buffer) throws IOException {
        if (inputStream == null || sourceInfo == null) {
            throw new IllegalStateException("input stream or source info not initialized");
        }

        byte[] bytes = new byte[buffer.length * 2 * sourceInfo.getNumberOfChannels()];
        int byteCount = inputStream.read(bytes, 0, bytes.length);
        if (byteCount == -1) {
            return 0;
        }
        int sampleCount = byteCount / 2 / sourceInfo.getNumberOfChannels();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < sampleCount; i++) {
            buffer[i] = 0;
            for (int j = 0; j < sourceInfo.getNumberOfChannels(); j++) {
                buffer[i] += PCMUtil.short2Float(byteBuffer.getShort()) / sourceInfo.getNumberOfChannels();
            }
        }
        return sampleCount;
    }

    private static void fillSourceInfo(DataInputStream inputStream, SourceInfo sourceInfo) throws IOException {
        if (inputStream == null) {
            throw new IllegalStateException("input stream is no initialized");
        }

        byte[] intValue = new byte[4];
        inputStream.skip(FORMAT_CHUNK_SIZE_OFFSET);

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

        forwardToDataChunk(inputStream);
        inputStream.read(intValue);
        sourceInfo.setDataSize(ByteBuffer.wrap(intValue).getInt());
        sourceInfo.init();
    }

    private static void forwardToDataChunk(InputStream inputStream) throws IOException {
        byte[] prev = new byte[2];
        byte[] curr = new byte[2];
        String data = "";
        while (!SUB_CHUNK_2_ID.equals(data)) {
            System.arraycopy(curr, 0, prev, 0, curr.length);
            if (inputStream.read(curr) <= 0)
                throw new IllegalArgumentException(String.format("unable to find data chunk with id %s", SUB_CHUNK_2_ID));
            data = new String(prev, StandardCharsets.UTF_8) + new String(curr, StandardCharsets.UTF_8);
        }
    }
}
