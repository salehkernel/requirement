package ir.salmanian.models;

/**
 * An enum for defining and showing values of evaluation method of a requirement.
 */
public enum EvaluationMethod {
    ANALYSIS("آنالیز"),
    SIMULATION("شبیه سازی"),
    INSPECTION("بازرسی"),
    SYSTEM_TEST("تست سیستم"),
    COMPONENT_TEST("تست کامپوننت");

    private EvaluationMethod(String label) {
        this.label = label;
    }

    public final String label;

    public String toString() {
        return label;
    }
}
