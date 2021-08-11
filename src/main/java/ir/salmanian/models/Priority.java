package ir.salmanian.models;

public enum Priority {
    KEY("کلیدی"),
    NECESSARY("ضروری"),
    OPTIONAL("اختیاری");
    private static final String nickname = "اولویت";

    private Priority(String label) {
        this.label = label;
    }

    public final String label;

    public static String getNickname() {
        return nickname;
    }

    public String toString() {
        return label;
    }
}