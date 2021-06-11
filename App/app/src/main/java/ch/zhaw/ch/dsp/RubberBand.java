package ch.zhaw.ch.dsp;

import android.util.Log;

import com.breakfastquay.rubberband.RubberBandStretcher;

import ch.zhaw.ch.ModuleInfo;
import ch.zhaw.ch.SourceInfo;
import ch.zhaw.ch.TrackInfo;
import ch.zhaw.ch.dsp.phase.PhaseResetType;
import ch.zhaw.ch.dsp.phase.TransientDetectionType;

/***
 * This audiotransformer uses the nativ client interface of RubberBand.
 */

public class RubberBand implements AudioTransformer {

    RubberBandStretcher rbs;
    TrackInfo trackInfo;
    SourceInfo sourceInfo;


    @Override
    public void init(ModuleInfo moduleInfo, TrackInfo trackInfo, SourceInfo sourceInfo) throws Exception {
        this.trackInfo = trackInfo;
        this.sourceInfo = sourceInfo;
        TransientDetectionType transientDetectionType = TransientDetectionType.getValue(moduleInfo.getTransientDetector());
        PhaseResetType phaseResetType = PhaseResetType.getValue(moduleInfo.getPhaseResetType());
        int options = 0;

        options += RubberBandStretcher.OptionProcessRealTime;
        if(transientDetectionType == TransientDetectionType.HIGH_FREQ){
            options += RubberBandStretcher.OptionDetectorSoft;
        }else if(transientDetectionType == TransientDetectionType.PERCUSSIVE){
            options += RubberBandStretcher.OptionDetectorPercussive;
        }else if(transientDetectionType == TransientDetectionType.COMPOUND){
            options += RubberBandStretcher.OptionDetectorCompound;
        }

        if(transientDetectionType == TransientDetectionType.NONE){
            options += RubberBandStretcher.OptionTransientsSmooth;
        }else if(phaseResetType ==PhaseResetType.BAND_LIMITED){
            options += RubberBandStretcher.OptionTransientsMixed;
        }else if(phaseResetType ==PhaseResetType.FULL_RANGE){
            options += RubberBandStretcher.OptionTransientsCrisp;
        }

        options += RubberBandStretcher.OptionPhaseLaminar;

        options += RubberBandStretcher.OptionFormantShifted;
        rbs = new RubberBandStretcher(sourceInfo.getSampleRate(), sourceInfo.getNumberOfChannels(), options, 1, trackInfo.getPitchShiftFactor());
    }

    @Override
    public float[] transformBuffered(float[] samples) {
        float[][] data = new float[1][samples.length];
        System.arraycopy(samples, 0, data[0], 0, samples.length);
        rbs.process(data, false);
        rbs.retrieve(data);
        return data[0];
    }

    @Override
    public float[] transformFrame(float[] frame) {
        return new float[0];
    }

    @Override
    public float[] test(float[] samples) {
        return new float[0];
    }
}
