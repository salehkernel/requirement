package ir.salmanian.controllers;

import ir.salmanian.models.Project;
import ir.salmanian.models.User;
import ir.salmanian.cells.ProjectCell;
import ir.salmanian.services.ProjectService;
import ir.salmanian.utils.UserHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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
        projectObservableList = FXCollections.observableArrayList();
        User user = UserHolder.getInstance().getUser();
        List<Project> projects = ProjectService.getInstance().getUserProjects(user);
        projectObservableList.addAll(projects);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectsListView.setItems(projectObservableList);
        projectsListView.setCellFactory(param -> new ProjectCell());
    }

    @FXML
    public void newProjectClick(ActionEvent event) {
        projectsListView.setEditable(true);
        projectObservableList.add(0, new Project().setEditable(true));
    }

    @FXML
    public void onSearchProjectClick(ActionEvent event) {
        List<Project> projects = ProjectService.getInstance().searchProjects(searchField.getText().trim());
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

}
