package ch.zhaw.ch.dsp.phase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/***
 * Extended enumerator for transient detection, that provides functionalities for listing and identifying
 * selections on the GUI.
 */
public enum TransientDetectionType {
    COMPOUND(0, "compound", "COMPOUND"),
    NONE(1, "none", "NONE"),
    PERCUSSIVE(2, "percussive", "PERCUSSIVE"),
    HIGH_FREQ(3, "high frequency", "HIGH_FREQ");

    private int index;
    private String displayName;
    private String identifier;

    /***
     * Get all values hold in this enumerator
     * @return enum list
     */
    public static List<TransientDetectionType> getValues() {
        return Arrays.stream(values()).collect(Collectors.toList());
    }

    /***
     * Search for an specific TransientDetectionType by its string identifier
     * @param identifier
     * @return a single TransientDetectionType
     */
    public static TransientDetectionType getValue(String identifier) {
        return Arrays.stream(values()).filter(p -> p.identifier.equals(identifier)).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("no phase shifter module of the name %s exists", identifier)));
    }

    /***
     * Search for an specific TransientDetectionType by its index in the list
     * @param index
     * @return a single TransientDetectionType
     */
    public static TransientDetectionType getValue(int index) {
        return Arrays.stream(values()).filter(p -> p.index == index).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("no phase shifter module of the name %s exists", index)));
    }

    /***
     * Enum constructor
     * @param index
     * @param displayName
     * @param identifier
     */
    TransientDetectionType(int index, String displayName, String identifier){
        this.index = index;
        this.displayName = displayName;
        this.identifier = identifier;
    }

    /***
     * Returns the index number of the currently hold TransientDetectionType
     * @return
     */
    public int getIndex() {
        return index;
    }

    /***
     * Returns the displayName string of the currently hold TransientDetectionType
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    /***
     * Returns the identifier string of the currently hold TransientDetectionType
     * @return
     */
    public String getIdentifier() {
        return identifier;
    }
}
