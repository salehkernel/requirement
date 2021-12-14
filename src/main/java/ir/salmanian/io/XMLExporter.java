package ir.salmanian.io;

import ir.salmanian.models.Project;

public class XMLExporter implements Exporter{
    Project project;

    public XMLExporter(Project project) {
        this.project = project;
    }

    @Override
    public void exportToFile() {
        System.out.println("export to xml");
    }
}
