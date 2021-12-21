package ir.salmanian.io;

import ir.salmanian.models.Project;
import ir.salmanian.models.Requirement;
import ir.salmanian.services.RequirementService;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.*;

/**
 * XMLExporter class is an implementation of {@link Exporter} interface.
 * This class is used to export project to an xml file.
 */
public class XMLExporter implements Exporter {
    Project project;

    public XMLExporter(Project project) {
        this.project = project;
    }

    /**
     * This overridden method is used to export project to an xml file.
     * see also {@link Exporter}
     */
    @Override
    public void exportToFile() {
        File file = chooseExportedXmlFile();
        if (file != null) {
            if (!file.getName().endsWith(".xml")) {
                file = new File(file.getAbsolutePath() + ".xml");
            }
            Set<Requirement> uniqueRequirements = getOrderedByLevelRequirements();
            for (Requirement requirement : uniqueRequirements) {
                requirement.getParents().forEach(parent -> parent.getParents().clear());
            }
            project.setRequirementList(new ArrayList<>(uniqueRequirements));
            try {
                JAXBContext context = JAXBContext.newInstance(Project.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(project, file);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to create a .xml file using file chooser.
     * @return the intended file.
     */
    private File chooseExportedXmlFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(String.format("%s.xml", project.getName()));
        fileChooser.setTitle("Export requirement to xml");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xml files (.xml)", "*.xml"));
        Stage stage = new Stage();
        return fileChooser.showSaveDialog(stage);
    }

    /**
     * This method is used to get project requirements in their level orders.
     * @return  a set of requirements.
     */
    private Set<Requirement> getOrderedByLevelRequirements() {
        Set<Requirement> finalRequirements = new LinkedHashSet<>();
        Set<Requirement> requirementParentSet = new LinkedHashSet<>();
        Set<Requirement> requirementChildrenSet = new LinkedHashSet<>();
        List<Requirement> requirementsList = RequirementService.getInstance().getRequirements(project);
        Iterator<Requirement> requirementIterator = requirementsList.iterator();
        while (requirementIterator.hasNext()) {
            Requirement requirement = requirementIterator.next();
            if (requirement.getParents().isEmpty()) {
                requirementParentSet.add(requirement);
            }
            requirementIterator.remove();
        }
        while (!requirementParentSet.isEmpty()) {
            finalRequirements.addAll(requirementParentSet);
            requirementIterator = requirementParentSet.iterator();
            while (requirementIterator.hasNext()) {
                Requirement parent = requirementIterator.next();
                List<Requirement> children = RequirementService.getInstance().getChildrenRequirements(parent);
                if (!children.isEmpty())
                    requirementChildrenSet.addAll(children);
                requirementIterator.remove();
            }
            requirementParentSet = requirementChildrenSet;
            requirementChildrenSet = new LinkedHashSet<>();
        }
        return finalRequirements;
    }
}
