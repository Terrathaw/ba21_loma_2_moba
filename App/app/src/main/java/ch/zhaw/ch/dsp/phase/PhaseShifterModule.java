package ch.zhaw.ch.dsp.phase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ch.zhaw.ch.dsp.RubberBand;

public enum PhaseShifterModule {
    DYNAMIC_PHASE_LOCKED_SHIFTER(0, DynamicPhaseLockedShifter.class.getSimpleName(), DynamicPhaseLockedShifter.class.getName()),
    BASIC_PHASE_SHIFTER(1, BasicPhaseShifter.class.getSimpleName(), BasicPhaseShifter.class.getName()),
    SCALED_PHASE_lOCKED_SHIFTER(2, ScaledPhaseLockedShifter.class.getSimpleName(), ScaledPhaseLockedShifter.class.getName()),
    RUBBERBAND(3, RubberBand.class.getSimpleName(), RubberBand.class.getName());

    public static List<PhaseShifterModule> getValues() {
        return Arrays.stream(values()).collect(Collectors.toList());
    }

    public static PhaseShifterModule getValue(String className) {
        return Arrays.stream(values()).filter(p -> p.className.equals(className)).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("no phase shifter module of the name %s exists", className)));
    }

    public static PhaseShifterModule getValue(int index) {
        if (index < 0 || index >= values().length)
            throw new IllegalArgumentException(String.format("the index %d is out of bounds"));
        return values()[index];
    }

    private int index;
    private String displayName;
    private String className;

    private PhaseShifterModule(int index, String displayName, String className) {
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
