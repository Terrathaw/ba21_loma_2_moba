package ch.zhaw.ch.visualization;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import ch.zhaw.ch.TrackInfo;
import ch.zhaw.ch.fft.FFT;

public class FrequencyVisualizer extends Visualizer {
    private TrackInfo info;

    private FFT fft;
    LineGraphSeries lineGraphSeries;
    GraphView graph;

    @Override
    public void init(TrackInfo info) {
        this.info = info;
        this.fft.init(info.getFrameSize());

        DataPoint[] dataPoints = new DataPoint[info.getFrameSize()];
        for (int i = 0; i < info.getFrameSize(); i++) {
            dataPoints[i] = new DataPoint(0, 0);
        }
        lineGraphSeries = new LineGraphSeries(dataPoints);
    }

    @Override
    public void setFFT(FFT fft) {
        this.fft = fft;
    }

    @Override
    public void setGraph(GraphView graph) {
        this.graph = graph;
        if (this.graph != null)
            graph.addSeries(lineGraphSeries);
    }

    @Override
    public void displaySamples(float[] samples) {
//        double frequencyResolution = info.getSampleRate()/2.0/info.getFrameSize();
//        float[] buffer;
//        for(int i = 0; info.getFrameSize() * (i+1) <=samples.length; i++){
//            buffer = Arrays.copyOfRange(samples, info.getFrameSize() * i, info.getFrameSize() * (i+1));
//            float[] magnitude = fft.forward(buffer).getMagnitude();
//            DataPoint[] dataPoints = new DataPoint[magnitude.length];
//            for (int j = 0; j < magnitude.length; j++) {
//                DataPoint a = new DataPoint(j * frequencyResolution, (10 * Math.log10(magnitude[j])));
//                //DataPoint a = new DataPoint(j , magnitude[j]);
//                //Log.v();
//                if(a != null)
//                    dataPoints[j] = a;
//            }
//            if(graph != null && dataPoints!= null) {
//                lineGraphSeries.resetData(dataPoints);
//            }
//        }
    }

}
