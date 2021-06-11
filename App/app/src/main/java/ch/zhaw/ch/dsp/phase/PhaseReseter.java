package ch.zhaw.ch.dsp.phase;

/***
 * Base abstract class for all pitch shift algorithms to pass functionnsa for transient detection
 * and band limiting.
 */

public abstract class PhaseReseter {
    protected PhaseResetType phaseResetType = PhaseResetType.BAND_LIMITED;
    protected float[] phaseTransformed;
    protected int frameSizeNyquist;
    protected int frameIndex;
    protected int bandLow;
    protected int bandHigh;
    protected int[] defaultMidRange;
    protected int[] currentMidRange;
    protected TransientDetection transientDetection;

    /***
     * Sets up the base class.
     * @param frameSize
     * @param frameSizeNyquist
     * @param sampleRate
     * @param transientDetectionType
     */
    protected void setup(int frameSize, int frameSizeNyquist, int sampleRate, TransientDetectionType transientDetectionType){
        bandLow = (int) Math.floor(150*frameSize / sampleRate);
        bandHigh = (int) Math.floor(1000*frameSize / sampleRate);
        defaultMidRange = new int [2];
        defaultMidRange[0] = 0;
        defaultMidRange[1] = frameSizeNyquist;
        currentMidRange = new int [2];
        transientDetection = new TransientDetection(frameSizeNyquist, transientDetectionType);
    }

    /***
     * Resets the phases according to the phase reset type and sets the current mid range
     * @param phase
     */
    protected void resetPhase(float[] phase){
        if(phaseResetType.equals(PhaseResetType.BAND_LIMITED)){
            for(int i = 0; i < bandLow; i++){
                phaseTransformed[i] = phase[i];
            }
            for(int i = bandHigh; i < frameSizeNyquist; i++){
                phaseTransformed[i] = phase[i];
            }
            currentMidRange[0] = bandLow;
            currentMidRange[1] = bandHigh;

        }else{
            phaseTransformed = phase;
            resetCurrentMidRange();
        }
    }

    /***
     * Sets the phase reset type
     * @param phaseResetType
     */
    public void setPhaseResetType(PhaseResetType phaseResetType) {
        this.phaseResetType = phaseResetType;
    }

    /***
     * Resets the currentMidRange to the defaultMidRange.
     */
    protected void resetCurrentMidRange(){
        currentMidRange[0] = defaultMidRange[0];
        currentMidRange[1] = defaultMidRange[1];
    }
}