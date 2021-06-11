package ch.zhaw.ch.io;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.zhaw.ch.ModuleInfo;
import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.TrackInfo;
import ch.zhaw.ch.dsp.PitchShifter;
import ch.zhaw.ch.dsp.phase.ScaledPhaseLockedShifter;
import ch.zhaw.ch.fft.WindowType;
import ch.zhaw.ch.util.ArrayUtil;

public class WaveWriterTest {
    String pathIn = "src\\test\\res\\audio\\in";
    String pathOut = "src\\test\\res\\audio\\out";
    int wavHeader = 40;

    @Test
    public void writeEmptyWavFile_isCorrect() throws IOException {
        String path = "src\\test\\res\\audio\\out";
        String filename = "file-writer-test-empty.wav";
        SourceInfo sourceInfo = new SourceInfo();
        sourceInfo.setSampleRate(22050);
        sourceInfo.setNumberOfChannels((short) 1);
        sourceInfo.setBitDepth((short) 16);

        TrackInfo info = new TrackInfo();
        info.setFrameSize(2048);
        info.setHopSizeFactor(4);
        info.setPitchShiftFactor(1.2f);
        info.setWindowType(WindowType.HANN);


        int sineFrequency = 440;
        int sineLength = 5;

        float[] timeline = ArrayUtil.range(sourceInfo.getSampleRate() * sineLength, 0, 1f / sourceInfo.getSampleRate());
        float[] sine = ArrayUtil.rangeOfValue(timeline.length + info.getFrameSize() * 2, 0f);

        System.arraycopy(timeline, 0, sine, info.getFrameSize(), timeline.length);
        for (int i = 0; i < sine.length; i++) {
            sine[i] = (float) Math.sin(2 * Math.PI * sineFrequency * sine[i]);
        }

        WaveWriter fileWriter = new WaveWriter();

        fileWriter.open(path, filename);
        fileWriter.writeHeader(sourceInfo);
        fileWriter.write(sine);
        fileWriter.close();
    }

    @Test
    public void writeFilesFromFolder() throws IOException {
        String phaseShifter = ScaledPhaseLockedShifter.class.getName();

        File folder = new File(pathIn);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println(pathIn+"\\"+fileEntry.getName());
            } else {
                writeFile(fileEntry.getName(), phaseShifter, 3);
            }
        }
    }

    private void writeFile(String filename, String phaseShifter, int halfToneShift) throws IOException {
        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.setPhaseShifter(phaseShifter);
        PitchShifter pitchShifter = new PitchShifter();

        System.out.println("Write file: "+filename);
        WaveReader fileReader = new WaveReader();
        WaveWriter fileWriter = new WaveWriter();
        SourceInfo sourceInfo = new SourceInfo();
        TrackInfo trackInfo = new TrackInfo();

        float[] buffer;

        try {
            fileReader.openFile(pathIn+"\\"+filename);
            fileWriter.open(pathOut, filename);
            byte[] header = fileReader.getByteHeader();
            int byteBufferSize = fileReader.setSourceInfo(sourceInfo, header);
            sourceInfo.setByteBufferSize(Math.max(trackInfo.getFrameSize() * 4 * sourceInfo.getNumberOfChannels(), byteBufferSize));
            trackInfo.setSampleBufferSize(sourceInfo.getSampleBufferSize(), sourceInfo.getNumberOfChannels());
            trackInfo.setHalfToneStepsToShift(halfToneShift);
            trackInfo.init();
            fileWriter.writeHeader(header);
            buffer = new float[40];
            pitchShifter.init(moduleInfo, trackInfo, sourceInfo);

            while(fileReader.read(buffer) !=  0){
                fileWriter.write(pitchShifter.transformBuffered(buffer));
            }
        } catch (FileNotFoundException e){

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileReader.close();
            fileWriter.close();
        }
    }
}
