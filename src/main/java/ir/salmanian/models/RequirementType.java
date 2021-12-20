package ir.salmanian.models;

/**
 * An enum for defining and showing values of type of a requirement.
 */
public enum RequirementType {
    FUNCTIONAL("کارکردی"),
    NON_FUNCTIONAL("غیر کارکردی");

    private RequirementType(String label) {
        this.label = label;
    }

    public final String label;

    public String toString() {
        return label;
    }
}
