package ir.salmanian.controllers;

import ir.salmanian.models.Requirement;
import ir.salmanian.services.RequirementService;
import ir.salmanian.utils.ProjectHolder;
import ir.salmanian.utils.RequirementHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * The controller class for SelectParent.fxml file which is used for
 * selecting some requirements a parents of another requirement.
 * The defined elements ids in fxml file are used in this class.
 * The elements on action method names in fxml file are defined and implemented in this class.
 */
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

    private ObservableList<Requirement> requirementObservableList;
    private Requirement requirementHolder;

    public SelectParentController() {
        requirementHolder = RequirementHolder.getInstance().getRequirement();
        requirementObservableList = FXCollections.observableArrayList();
        List<Requirement> requirements = RequirementService.getInstance().getRequirements(ProjectHolder.getInstance().getProject());
        List<Requirement> children = /*requirementHolder.getChildren()*/RequirementService.getInstance().getChildrenRequirements(requirementHolder);
        requirements.remove(requirementHolder);
        if (children != null)
            requirements.removeAll(children);
        /*for (Requirement req : requirements) {
            if (req.getLevel() == requirementHolder.getLevel() - 1) {
                requirementObservableList.add(req);
            }
        }*/
        requirementObservableList.addAll(requirements);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requirementListView.setItems(requirementObservableList);
        requirementListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        requirementListView.getItems().filtered(requirement -> requirementHolder.getParents().contains(requirement))
                .forEach(requirement -> requirementListView.getSelectionModel().select(requirement));
        requirementListView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                requirementListView.getParent().fireEvent(event);
            }
        });
        /*requirementListView.setCellFactory(
                CheckBoxListCell.forListView(new Callback<Requirement, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(Requirement param) {
                        BooleanProperty observable = new SimpleBooleanProperty();

                        if (requirementHolder.getParents().contains(param)) {
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
        );*/

    }

    @FXML
    public void onCancelClick(ActionEvent event) throws IOException {
        if (requirementHolder.getId() == null)
            ScreenController.getInstance().closeStage("newSelectParentStage");
        else
            ScreenController.getInstance().closeStage(String.format("selectParentStage-%s", requirementHolder.getId()));
    }

    @FXML
    public void onAddParentsClick(ActionEvent event) throws IOException {
//        Requirement[] a = selectedRequirements.toArray(new Requirement[]{});
        requirementHolder.setParents(/*new ArrayList<>(selectedRequirements)*/requirementListView.getSelectionModel()
                .getSelectedItems());
        if (requirementHolder.getId() == null) {
            ScreenController.getInstance().addScene("newRequirementFormScene", "RequirementForm.fxml");
            ScreenController.getInstance().activateScene("newRequirementFormScene", ScreenController.getInstance().getStage("newRequirementFormStage"));
            ScreenController.getInstance().closeStage("newSelectParentStage");
        } else {
            ScreenController.getInstance().addScene(String.format("requirementFormScene-%s", requirementHolder.getId()), "RequirementForm.fxml");
            ScreenController.getInstance().activateScene(String.format("requirementFormScene-%s", requirementHolder.getId()), ScreenController.getInstance().getStage(String.format("requirementFormStage-%s", requirementHolder.getId())));
            ScreenController.getInstance().closeStage(String.format("selectParentStage-%s", requirementHolder.getId()));
        }
    }

    @FXML
    public void onSearchRequirementClick(ActionEvent event) {
        List<Requirement> requirements = RequirementService.getInstance()
                .searchRequirement(searchField.getText().trim(), ProjectHolder.getInstance().getProject());
        List<Requirement> children = RequirementService.getInstance().getChildrenRequirements(requirementHolder);
        requirements.remove(requirementHolder);
        if (children != null)
            requirements.removeAll(children);
        requirementObservableList.clear();
        /*for (Requirement req : requirements) {
            if (req.getLevel() == requirementHolder.getLevel() - 1) {
                requirementObservableList.add(req);
            }
        }*/
        requirementObservableList.addAll(requirements);
        requirementListView.setItems(requirementObservableList);
    }
}
