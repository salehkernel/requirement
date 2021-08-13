package ir.salmanian.utils;

import ir.salmanian.models.Requirement;

public class RequirementHolder {
    private Requirement requirement;
    private static RequirementHolder instance;

    private RequirementHolder() {

    }

    public static RequirementHolder getInstance() {
        if (instance == null)
            instance = new RequirementHolder();
        return instance;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }
}
