package ir.salmanian.models;

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
