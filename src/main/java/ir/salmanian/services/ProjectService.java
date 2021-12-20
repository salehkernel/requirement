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

    public List<Project> getUserProjects(User user) {
        return DatabaseManagement.getInstance().getUserProjects(user);
    }

    public void saveProject(Project project) {
        DatabaseManagement.getInstance().saveProject(project);
    }

    public void deleteProject(Project project) {
        DatabaseManagement.getInstance().deleteProject(project);
    }

    public List<Project> searchProjects(String text, User user) {
        return DatabaseManagement.getInstance().searchProjects(text, user);
    }
}
