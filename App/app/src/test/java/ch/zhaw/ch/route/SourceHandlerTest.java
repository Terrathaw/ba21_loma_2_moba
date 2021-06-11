package ch.zhaw.ch.route;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SourceHandlerTest {

    @Test
    public void BufferLoading() throws IOException {
//        Track mockTrack = Mockito.mock(Track.class);
//        TrackInfo mockTrackInfo = Mockito.mock(TrackInfo.class);
//        SyncedCycleBuffer mockBuffer = Mockito.mock(SyncedCycleBuffer.class);
//        SignalInputStream mockStream = Mockito.mock(SignalInputStream.class);
//        int buffersize = 10;
//        float[] buffer = new float[buffersize];
//
//        Mockito.when(mockTrack.getInfo()).thenReturn(mockTrackInfo);
//        Mockito.when(mockTrackInfo.getByteBufferSize()).thenReturn(buffersize);
//        Mockito.when(mockTrack.getSignalInputStream()).thenReturn(mockStream);
//        Mockito.when(mockStream.readFloats(buffersize)).thenReturn(buffer);
//        Mockito.when(mockBuffer.hasAvailableSpace()).thenReturn(false);
//        Mockito.when(mockBuffer.tryLock()).thenReturn(false);
//
//        SourceHandler sourceHandler = new SourceHandler(mockTrack, mockBuffer);
//
//        Mockito.verify(mockBuffer, Mockito.times(0)).writeNextBuffer(buffer);
//
//
//        Mockito.when(mockBuffer.hasAvailableSpace()).thenReturn(true);
//        Mockito.when(mockBuffer.tryLock()).thenReturn(false);
//        Mockito.verify(mockBuffer, Mockito.times(0)).writeNextBuffer(buffer);
//
//
//        Mockito.when(mockBuffer.hasAvailableSpace()).thenReturn(true);
//        Mockito.when(mockBuffer.tryLock()).thenReturn(true);
//        Mockito.verify(mockBuffer, Mockito.times(1)).writeNextBuffer(buffer);
//        Mockito.verify(mockBuffer, Mockito.times(1)).writeNextBuffer(buffer);
//        Mockito.verify(mockBuffer, Mockito.times(1)).writeNextBuffer(buffer);
//
//        Mockito.when(mockBuffer.hasAvailableSpace()).thenReturn(false);
//        Mockito.verify(mockBuffer, Mockito.times(0)).writeNextBuffer(buffer);
        Assert.assertTrue(false);
    }
}
