package ir.salmanian.utils;

import ir.salmanian.models.Project;

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
