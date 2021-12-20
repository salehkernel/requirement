package ir.salmanian.models;

/**
 * An enum for defining and showing values of review status of a requirement.
 */
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
