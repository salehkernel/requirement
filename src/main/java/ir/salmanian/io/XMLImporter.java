package ir.salmanian.io;

import ir.salmanian.models.Project;
import ir.salmanian.services.ProjectService;
import ir.salmanian.services.RequirementService;
import ir.salmanian.utils.UserHolder;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * XMLImporter class is an implementation of {@link Importer} interface.
 * This class is used to import a project to application from an xml file.
 */
public class XMLImporter implements Importer {
    @Override
    public void importFromFile(File importFile) {
        try {
            JAXBContext context = JAXBContext.newInstance(Project.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Project project = (Project) unmarshaller.unmarshal(importFile);
            project.setCreator(UserHolder.getInstance().getUser());
            ProjectService.getInstance().saveProject(project);
            project.getRequirementList().forEach(requirement -> {
                requirement.setProject(project);
                RequirementService.getInstance().saveRequirement(requirement);
            });
        } catch (JAXBException e) {
            Dialog dialog = new Dialog();
            ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            dialog.setContentText("فایل انتخاب شده قابل استفاده نمی باشد.\nلطفا فایل دیگری را انتخاب نمایید.");
            dialog.getDialogPane().getButtonTypes().add(ok);
            dialog.showAndWait();
        }
    }
}
