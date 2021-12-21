package ir.salmanian.utils;

import ir.salmanian.models.Project;

/**
 * ProjectHolder class is used to store project object in memory when it is selected in projectsListView
 * of {@link ir.salmanian.controllers.ProjectsController} class in order to be used in future required
 * times and positions like adding requirement to intended project or searching in requirements of that project.
 * This class uses singleton design pattern.
 */
public class ProjectHolder {
    private Project project;
    private static ProjectHolder instance;

    private ProjectHolder() {

    }

    public static ProjectHolder getInstance() {
        if (instance == null)
            instance = new ProjectHolder();
        return instance;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return this.project;
    }
}
