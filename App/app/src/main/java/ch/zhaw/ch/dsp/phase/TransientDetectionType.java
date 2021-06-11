package ch.zhaw.ch.dsp.phase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TransientDetectionType {
    COMPOUND(0, "compound", "COMPOUND"),
    NONE(1, "none", "NONE"),
    PERCUSSIVE(2, "percussive", "PERCUSSIVE"),
    HIGH_FREQ(3, "high frequency", "HIGH_FREQ");

    private int index;
    private String displayName;
    private String className;

    public static List<TransientDetectionType> getValues() {
        return Arrays.stream(values()).collect(Collectors.toList());
    }

    public static TransientDetectionType getValue(String className) {
        return Arrays.stream(values()).filter(p -> p.className.equals(className)).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("no phase shifter module of the name %s exists", className)));
    }
    public static TransientDetectionType getValue(int className) {
        return Arrays.stream(values()).filter(p -> p.index == className).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("no phase shifter module of the name %s exists", className)));
    }

    private TransientDetectionType(int index, String displayName, String className){
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
