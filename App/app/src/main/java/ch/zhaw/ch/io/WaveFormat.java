package ch.zhaw.ch.io;

import java.util.Arrays;

public enum WaveFormat {
    WAVE_FORMAT_UNKNOWN(0),
    WAVE_FORMAT_PCM(1),
    WAVE_FORMAT_IEEE_FLOAT(3),
    WAVE_FORMAT_ALAW(6),
    WAVE_FORMAT_MULAW(7),
    WAVE_FORMAT_EXTENSIBLE(65534);

    private int _val;

    WaveFormat(int val) {
        _val = val;
    }

    public int getValue() {
        return _val;
    }

    public static WaveFormat parse(int val) {
        return Arrays.stream(values())
                .filter(v -> v.getValue() == val)
                .findFirst().orElse(null);
    }
}
