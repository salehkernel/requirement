package ir.salmanian.controllers;

import ir.salmanian.models.Priority;
import ir.salmanian.models.Requirement;
import ir.salmanian.services.RequirementService;
import ir.salmanian.utils.ProjectHolder;
import ir.salmanian.utils.RequirementHolder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class RequirementsController implements Initializable {
    @FXML
    private TreeView<String> filterTreeView;
    @FXML
    private TextField searchRequirementField;
    @FXML
    private Button filterBtn;
    @FXML
    private TreeView<Requirement> requirementTreeView;
    private Set<Requirement> selectedRequirements = new HashSet<>();
    private ObservableList<Requirement> requirementObservableList;
    private ChangeListener<Boolean> treeViewEventListener;
    List<TreeItem<String>> filters = new ArrayList<>();

    public RequirementsController() {
        TreeItem<String> rootItem = new TreeItem<>(Priority.getNickname());
        rootItem.setExpanded(true);
        for (int i = 0; i < Priority.values().length; i++) {
            Priority[] arrays = Priority.values();
            TreeItem<String> item = new TreeItem<>(arrays[i].label);
            rootItem.getChildren().add(item);
        }
        filters.add(rootItem);
        requirementObservableList = FXCollections.observableArrayList();
        List<Requirement> requirements = RequirementService.getInstance().getRequirements(ProjectHolder.getInstance().getProject());
        for (Requirement requirement : requirements) {
            if (requirement.getLevel() == 1) {
                requirementObservableList.add(requirement);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        treeViewEventListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                BooleanProperty booleanProperty = (BooleanProperty) observable;
                TreeItem<Requirement> parentItem = (TreeItem<Requirement>) booleanProperty.getBean();
                if (newValue) {
                    parentItem.getChildren().clear();
                    List<Requirement> childrenRequirements = RequirementService.getInstance().getChildrenRequirements(parentItem.getValue());
                    for (Requirement child : childrenRequirements) {
                        TreeItem<Requirement> childItem = new CheckBoxTreeItem<>(child);
                        childItem.expandedProperty().addListener(this::changed);
                        childItem.getChildren().add(new CheckBoxTreeItem<>());
                        parentItem.getChildren().add(childItem);
                    }
                }
            }
        };


        prepareTreeView();

        requirementTreeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    try {
                        Requirement requirement = requirementTreeView.getSelectionModel().getSelectedItem().getValue();
                        RequirementHolder.getInstance().setRequirement(requirement);
                        Stage stage = ScreenController.getInstance().openNewStage(String.format("requirementFormStage-%d", requirement.getId()));
                        ScreenController.getInstance().addScene(String.format("requirementFormScene-%d", requirement.getId()), "RequirementForm.fxml");
                        ScreenController.getInstance().activateScene(String.format("requirementFormScene-%d", requirement.getId()), stage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        TreeItem<String> r = new TreeItem<String>();
        r.getChildren().addAll(filters);

        filterTreeView.setRoot(r);
        filterTreeView.setShowRoot(false);
        filterTreeView.setCellFactory(param -> new CheckBoxTreeCell<>());
    }

    private void prepareTreeView() {
        TreeItem<Requirement> rootItem = new TreeItem<>();
        for (Requirement req : requirementObservableList) {
            TreeItem<Requirement> item = new TreeItem<>(req);
            item.getChildren().add(new TreeItem<>());
            item.expandedProperty().addListener(treeViewEventListener);
            rootItem.getChildren().add(item);
        }
        requirementTreeView.setRoot(rootItem);
        requirementTreeView.setShowRoot(false);
    }

    @FXML
    public void onNewRequirementClick(ActionEvent event) throws IOException {
        RequirementHolder.getInstance().setRequirement(new Requirement());
        Stage stage = ScreenController.getInstance().openNewStage("newRequirementFormStage");
        if (stage.getScene() == null) {
            ScreenController.getInstance().addScene("newRequirementFormScene", "RequirementForm.fxml");
            ScreenController.getInstance().activateScene("newRequirementFormScene", stage);
        }
    }

    @FXML
    public void onReturnClick(ActionEvent event) throws IOException {
        ScreenController.getInstance().activateScene("projectsScene", ScreenController.getInstance().getMainStage());
    }

    @FXML
    public void onSearchClick(ActionEvent event) {
        if (searchRequirementField.getText().trim().isEmpty()) {
            requirementObservableList.clear();
            List<Requirement> requirements = RequirementService.getInstance().getRequirements(ProjectHolder.getInstance().getProject());
            for (Requirement requirement : requirements) {
                if (requirement.getLevel() == 1) {
                    requirementObservableList.add(requirement);
                }
            }
            prepareTreeView();
            return;
        }
        List<Requirement> requirements = RequirementService.getInstance()
                .searchRequirement(searchRequirementField.getText().trim(), ProjectHolder.getInstance().getProject());
        requirementObservableList.clear();
        requirementObservableList.addAll(requirements);
        prepareTreeView();
    }

    @FXML
    public void onFilterClick() {
        System.out.println(filterTreeView.getSelectionModel().getSelectedItems());
    }
}
