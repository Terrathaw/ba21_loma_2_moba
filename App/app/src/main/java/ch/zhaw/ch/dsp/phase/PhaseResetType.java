package ch.zhaw.ch.dsp.phase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PhaseResetType {
    BAND_LIMITED(0, "band limited", "BAND_LIMITED"),
    FULL_RANGE(1, "full range", "FULL_RANGE");
    private int index;
    private String displayName;
    private String className;

    public static List<PhaseResetType> getValues() {
        return Arrays.stream(values()).collect(Collectors.toList());
    }

    public static PhaseResetType getValue(String className) {
        return Arrays.stream(values()).filter(p -> p.className.equals(className)).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("no phase shifter module of the name %s exists", className)));
    }
    public static PhaseResetType getValue(int className) {
        return Arrays.stream(values()).filter(p -> p.index == className).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("no phase shifter module of the name %s exists", className)));
    }

    private PhaseResetType(int index, String displayName, String className){
        this.index = index;
        this.displayName = displayName;
        this.className = className;
    }

    public int getIndex() {
        return index;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getClassName() {
        return className;
    }
}
