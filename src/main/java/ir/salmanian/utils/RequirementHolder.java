package ir.salmanian.utils;

import ir.salmanian.models.Requirement;

/**
 * RequirementHolder class is used to store Requirement object in memory when it is selected in requirementsTreeView
 * of {@link ir.salmanian.controllers.RequirementsController} class or when selectTemplateBtn or selectParentBtn
 * of {@link ir.salmanian.controllers.RequirementFormController} is clicked, in order to be used in future required
 * times and positions like modifying requirement or setting title from templates or assigning parents for
 * intended requirement.
 * This class uses singleton design pattern.
 */
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
