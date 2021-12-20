package ir.salmanian.services;

import ir.salmanian.db.DatabaseManagement;
import ir.salmanian.models.Project;
import ir.salmanian.models.Requirement;
import ir.salmanian.utils.ProjectHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A service layer class for handling requirements.
 * this class uses singleton design pattern.
 */

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

    public List<Requirement> searchRequirement(String text, Project project) {
        return DatabaseManagement.getInstance().searchRequirements(text, project);
    }

    public void deleteRequirement(Requirement requirement) {
        DatabaseManagement.getInstance().deleteRequirement(requirement);
    }

    public List<Requirement> getChildrenRequirements(Requirement requirement) {
        if (requirement.getId() == null)
            return new ArrayList<>();
        return DatabaseManagement.getInstance().getChildrenRequirements(requirement);
    }

    public Integer getMaxLevel(Project project) {
        return DatabaseManagement.getInstance().getMaxLevel(project);
    }

    public List<Requirement> filter(Field field, List<Object> values) {
        return DatabaseManagement.getInstance().filter(ProjectHolder.getInstance().getProject(), field, values);
    }
}
