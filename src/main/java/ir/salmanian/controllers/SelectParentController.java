package ir.salmanian.controllers;

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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SelectParentController implements Initializable {
    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    @FXML
    private ListView<Requirement> requirementListView;
    @FXML
    private Button addBtn;
    @FXML
    private Button cancelBtn;

    private Set<Requirement> selectedRequirements = new HashSet<>();

    private ObservableList<Requirement> requirementObservableList;

    public SelectParentController() {
        requirementObservableList = FXCollections.observableArrayList();
        List<Requirement> requirements = RequirementService.getInstance().getRequirements(ProjectHolder.getInstance().getProject());
        requirements.remove(RequirementHolder.getInstance().getRequirement());
        requirementObservableList.addAll(requirements);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requirementListView.setItems(requirementObservableList);
        requirementListView.setCellFactory(
                CheckBoxListCell.forListView(new Callback<Requirement, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(Requirement param) {
                        BooleanProperty observable = new SimpleBooleanProperty();

                        if (RequirementHolder.getInstance().getRequirement().getParents().contains(param)) {
                            observable.setValue(true);
                            selectedRequirements.add(param);
                        }

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
                })
        );
    }

    @FXML
    public void onCancelClick(ActionEvent event) throws IOException {
        ScreenController.getInstance().addScreen("requirementForm", "../ui/RequirementForm.fxml");
        ScreenController.getInstance().activate("requirementForm");
    }

    @FXML
    public void onAddParentsClick(ActionEvent event) throws IOException {
        Requirement[]a = selectedRequirements.toArray(new Requirement[]{});
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i] + " " +  a[0].equals(a[i]));
        }
        RequirementHolder.getInstance().getRequirement().setParents(new ArrayList<>(selectedRequirements));
        ScreenController.getInstance().addScreen("requirementForm", "../ui/RequirementForm.fxml");
        ScreenController.getInstance().activate("requirementForm");
    }

    @FXML
    public void onSearchRequirementClick(ActionEvent event) {
        List<Requirement> requirements = RequirementService.getInstance()
                .searchRequirement(searchField.getText().trim(), ProjectHolder.getInstance().getProject());
        requirements.remove(RequirementHolder.getInstance().getRequirement());
        requirementObservableList.clear();
        requirementObservableList.addAll(requirements);
        requirementListView.setItems(requirementObservableList);
    }
}
