package ir.salmanian.models;

/**
 * An enum for defining and showing values of change of a requirement.
 */
public enum Change {
    CONCRETE("سیمانی"),
    FREQUENCY("بسامدی");

    private Change(String label) {
        this.label = label;
    }

    public final String label;

    public String toString() {
        return label;
    }
}
