package ir.salmanian.models;

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
