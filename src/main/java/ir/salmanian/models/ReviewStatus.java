package ir.salmanian.models;

public enum ReviewStatus {
    REJECT("رد"),
    ACCEPT("قبول"),
    WAITING("انتظار");

    private ReviewStatus(String label) {
        this.label = label;
    }

    public final String label;

    public String toString() {
        return label;
    }
}
