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

    /**
     * This method is used to get all requirements of a project
     *
     * @param project the intended project
     * @return the list of requirements of the project
     */
    public List<Requirement> getRequirements(Project project) {
        return DatabaseManagement.getInstance().getProjectRequirements(project);
    }

    /**
     * This method is used to save a requirement.
     *
     * @param requirement the intended requirement
     */
    public void saveRequirement(Requirement requirement) {
        DatabaseManagement.getInstance().saveRequirement(requirement);
    }

    /**
     * This method is used to search between the requirements of a project
     *
     * @param text    the search phrase
     * @param project the intended project which search process should be applied to its requirements
     * @return a list of requirements which are the result of search
     */
    public List<Requirement> searchRequirement(String text, Project project) {
        return DatabaseManagement.getInstance().searchRequirements(text, project);
    }

    /**
     * This method is used delete a requirement.
     *
     * @param requirement the intended requirement
     */
    public void deleteRequirement(Requirement requirement) {
        DatabaseManagement.getInstance().deleteRequirement(requirement);
    }

    /**
     * This method is used to find children of a requirement.
     *
     * @param requirement the intended requirement which its children are expected.
     * @return a list of requirements whose parents include the intended requirement
     */
    public List<Requirement> getChildrenRequirements(Requirement requirement) {
        if (requirement.getId() == null)
            return new ArrayList<>();
        return DatabaseManagement.getInstance().getChildrenRequirements(requirement);
    }

    /**
     * This method is used to find the maximum level of requirements of a project.
     *
     * @param project the intended project
     * @return an Integer that shows the maximum level of the project
     */
    public Integer getMaxLevel(Project project) {
        return DatabaseManagement.getInstance().getMaxLevel(project);
    }

    /**
     * This method is used to filter requirements based on the intended field of Requirement class
     * and a list of values which are expected the result requirements specified field has one of them.
     * @param field the intended field of {@link Requirement} class
     * @param values the list of values which are expected the value of specified filed of result
     *               requirements be one of them.
     * @return a list of requirements that match the filter
     */
    public List<Requirement> filter(Field field, List<Object> values) {
        return DatabaseManagement.getInstance().filter(ProjectHolder.getInstance().getProject(), field, values);
    }
}
