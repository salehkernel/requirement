package ir.salmanian.models;

public enum EvaluationStatus {
    REJECT("رد"),
    ACCEPT("قبول"),
    WAITING("انتظار"),
    MET("ملاقات شده");

    EvaluationStatus(String label) {
        this.label = label;
    }

    public final String label;

    public String toString() {
        return label;
    }
}
