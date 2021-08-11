package ir.salmanian.controllers;

import ir.salmanian.models.Priority;
import ir.salmanian.models.Requirement;
import ir.salmanian.services.RequirementService;
import ir.salmanian.utils.ProjectHolder;
import ir.salmanian.utils.RequirementHolder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;

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
    private ListView<Requirement> requirementListView;
    private Set<Requirement> selectedRequirements = new HashSet<>();
    private ObservableList<Requirement> requirementObservableList;
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
        requirementObservableList.addAll(requirements);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requirementListView.setItems(requirementObservableList);
        requirementListView.setCellFactory(CheckBoxListCell.forListView(new Callback<Requirement, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Requirement param) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            selectedRequirements.add(param);
                        } else {
                            selectedRequirements.remove(param);
                        }

                    }
                });
                return observable;
            }
        }, new StringConverter<Requirement>() {
            @Override
            public String toString(Requirement object) {
                return object.toString();
            }

            @Override
            public Requirement fromString(String string) {
                return null;
            }
        }));
        requirementListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    try {
                        RequirementHolder.getInstance().setRequirement(requirementListView.getSelectionModel().getSelectedItem());
                        Requirement req = RequirementHolder.getInstance().getRequirement();
                        ScreenController.getInstance().addScreen(req.getPrefix() +"-" + req.getId() , "../ui/RequirementForm.fxml");
                        ScreenController.getInstance().openNewWindow(req.getPrefix() +"-" + req.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    ScreenController.getInstance().activate("requirementForm");

                }
            }
        });
        TreeItem<String> r = new TreeItem<String>();
        r.getChildren().addAll(filters);

        filterTreeView.setRoot(r);
        filterTreeView.setShowRoot(false);
        filterTreeView.setCellFactory(param -> new CheckBoxTreeCell<>());
    }

    @FXML
    public void onNewRequirementClick(ActionEvent event) throws IOException {
        RequirementHolder.getInstance().setRequirement(new Requirement().setParents(new ArrayList<>(selectedRequirements)));
        ScreenController.getInstance().addScreen("requirementForm", "../ui/RequirementForm.fxml");
        ScreenController.getInstance().activate("requirementForm");
    }

    @FXML
    public void onReturnClick(ActionEvent event) throws IOException {
        RequirementHolder.getInstance().setRequirement(null);
        ScreenController.getInstance().addScreen("projects", "../ui/Projects.fxml");
        ScreenController.getInstance().activate("projects");
    }

    @FXML
    public void onSearchClick(ActionEvent event) {
        List<Requirement> requirements = RequirementService.getInstance()
                .searchRequirement(searchRequirementField.getText().trim(),ProjectHolder.getInstance().getProject());
        requirementObservableList.clear();
        requirementObservableList.addAll(requirements);
        requirementListView.setItems(requirementObservableList);
    }

    @FXML
    public void onFilterClick() {
        System.out.println(filterTreeView.getSelectionModel().getSelectedItems());
    }
}
