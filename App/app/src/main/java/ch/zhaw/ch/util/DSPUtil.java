package ch.zhaw.ch.util;

public class DSPUtil {
    public final static float PI = (float) Math.PI;

    public static float princarg(float phase) {
        return phase - 2 * PI * Math.round(phase/(2*PI));
    }

    public static void princarg(float[] phase) {
        for (int i = 0; i < phase.length; i++) {
            phase[i] = (float) princarg(phase[i]);
        }
    }
}
