package ir.salmanian.services;

import ir.salmanian.db.DatabaseManagement;
import ir.salmanian.models.Project;
import ir.salmanian.models.User;

import java.util.List;

/**
 * A service layer class for handling projects.
 * This class uses singleton design pattern.
 */
public class ProjectService {
    private static ProjectService instance;

    private ProjectService() {
    }

    public static ProjectService getInstance() {
        if (instance == null) {
            instance = new ProjectService();
        }
        return instance;
    }

    /**
     * This method is used to get all projects of an intended user.
     * @param user the intended user
     * @return the list of intended user projects
     */
    public List<Project> getUserProjects(User user) {
        return DatabaseManagement.getInstance().getUserProjects(user);
    }
    /**
     * This method is used to save save an intended project.
     * @param project the intended project
     */
    public void saveProject(Project project) {
        DatabaseManagement.getInstance().saveProject(project);
    }

    /**
     * This method is used to delete a project.
     * @param project the intended project
     */
    public void deleteProject(Project project) {
        DatabaseManagement.getInstance().deleteProject(project);
    }

    /**
     * This method is used to search between projects of a user
     * @param text the search phrase
     * @param user the user which the search process should be applied in his/her projects
     * @return the list op projects which are the result of search
     */
    public List<Project> searchProjects(String text, User user) {
        return DatabaseManagement.getInstance().searchProjects(text, user);
    }
}
