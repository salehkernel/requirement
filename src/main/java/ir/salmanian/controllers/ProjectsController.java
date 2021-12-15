package ir.salmanian.controllers;

import ir.salmanian.cells.ProjectCell;
import ir.salmanian.io.Importer;
import ir.salmanian.io.XMLImporter;
import ir.salmanian.models.Project;
import ir.salmanian.models.User;
import ir.salmanian.services.ProjectService;
import ir.salmanian.utils.ProjectHolder;
import ir.salmanian.utils.UserHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectsController implements Initializable {
    @FXML
    private TextField searchField;
    @FXML
    private Button logoutBtn;
    @FXML
    private ListView<Project> projectsListView;
    private ObservableList<Project> projectObservableList;

    public ProjectsController() {
        refreshProjectsList();
    }

    private void refreshProjectsList() {
        projectObservableList = FXCollections.observableArrayList();
        User user = UserHolder.getInstance().getUser();
        List<Project> projects = ProjectService.getInstance().getUserProjects(user);
        projectObservableList.addAll(projects);
    }

    public static void openProject(Project project) {
        try {
            ProjectHolder.getInstance().setProject(project);
            ScreenController.getInstance().addScene("requirementsScene", "Requirements.fxml");
            ScreenController.getInstance().activateScene("requirementsScene", ScreenController.getInstance().getMainStage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectsListView.setItems(projectObservableList);
        projectsListView.setCellFactory(param -> new ProjectCell());
        projectsListView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        if (!projectsListView.getSelectionModel().isEmpty()) {
                            openProject(projectsListView.getSelectionModel().getSelectedItem());
                        }
                        break;
                    case ESCAPE:
                        if (!projectsListView.getSelectionModel().isEmpty() ||
                                projectsListView.getFocusModel().getFocusedItem() != null) {
                            projectsListView.getSelectionModel().clearSelection();
                        } else {
                            projectsListView.getParent().fireEvent(event);
                        }
                }
            }
        });
    }

    @FXML
    public void newProjectClick(ActionEvent event) {
        projectsListView.setEditable(true);
        projectObservableList.add(0, new Project().setEditable(true));
    }

    @FXML
    public void onSearchProjectClick(ActionEvent event) {
        List<Project> projects = ProjectService.getInstance()
                .searchProjects(searchField.getText().trim(), UserHolder.getInstance().getUser());
        projectObservableList.clear();
        projectObservableList.addAll(projects);
        projectsListView.setItems(projectObservableList);
    }

    @FXML
    public void onLogoutClick(ActionEvent event) throws IOException {
        ScreenController.getInstance().addScene("loginScene", "Login.fxml");
        ScreenController.getInstance().activateScene("loginScene", ScreenController.getInstance().getMainStage());
        UserHolder.getInstance().setUser(null);
    }

    @FXML
    public void onImportClick(ActionEvent event) {
        Importer xmlImporter = new XMLImporter();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("import project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("xml files (*.xml)", "*.xml"));
        Stage stage = new Stage();
        File importingFile = fileChooser.showOpenDialog(stage);
        xmlImporter.importFromFile(importingFile);
        refreshProjectsList();
        projectsListView.setItems(projectObservableList);
        projectsListView.refresh();
    }

}
