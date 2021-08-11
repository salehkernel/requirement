package ir.salmanian.services;

import ir.salmanian.db.DatabaseManagement;
import ir.salmanian.models.Project;
import ir.salmanian.models.Requirement;

import java.util.ArrayList;
import java.util.List;

public class RequirementService {
    private static RequirementService instance;

    private RequirementService() {

    }

    public static RequirementService getInstance() {
        if (instance == null)
            instance = new RequirementService();
        return instance;
    }

    public List<Requirement> getRequirements(Project project) {
        return DatabaseManagement.getInstance().getProjectRequirements(project);
    }

    public void saveRequirement(Requirement requirement) {
        DatabaseManagement.getInstance().saveRequirement(requirement);
    }

    public List<Requirement> searchRequirement(String text,Project project) {
        return DatabaseManagement.getInstance().searchRequirements(text,project);
    }

    public void deleteRequirement(Requirement requirement) {
        DatabaseManagement.getInstance().deleteRequirement(requirement);
    }

    public List<Requirement> getChildrenRequirements(Requirement requirement) {
        if(requirement.getId() == null)
            return new ArrayList<>();
        return DatabaseManagement.getInstance().getChildrenRequirements(requirement);
    }
}
