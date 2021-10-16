package ir.salmanian.controllers;

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
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.commons.lang.WordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RequirementsController implements Initializable {

    @FXML
    private TextField searchRequirementField;

    @FXML
    private TreeView<Requirement> requirementTreeView;
    @FXML
    private Button searchBtn;
    private ObservableList<Requirement> requirementObservableList;
    private ChangeListener<Boolean> treeViewChangeListener;

    public RequirementsController() {

        loadRequirements();
    }

    private void loadRequirements() {
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
        treeViewChangeListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                BooleanProperty booleanProperty = (BooleanProperty) observable;
                TreeItem<Requirement> parentItem = (TreeItem<Requirement>) booleanProperty.getBean();
                if (newValue) {
                    parentItem.getChildren().clear();
                    List<Requirement> childrenRequirements = RequirementService.getInstance().getChildrenRequirements(parentItem.getValue());
                    for (Requirement child : childrenRequirements) {
                        TreeItem<Requirement> childItem = new TreeItem<>(child);
                        childItem.expandedProperty().addListener(this::changed);
                        childItem.getChildren().add(new TreeItem<>());
                        parentItem.getChildren().add(childItem);
                    }
                }
            }
        };

        prepareTreeView();
        requirementTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        /*requirementTreeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
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


                } else if (event.isControlDown()) {
                    requirementTreeView.refresh();
                }
            }
        });*/

        requirementTreeView.setCellFactory(param -> {
            TreeCell<Requirement> treeCell = new TreeCell<Requirement>() {
                @Override
                public void updateItem(Requirement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setText(item.toString());
                        updateTreeItem(getTreeItem());
                        EventDispatcher eventDispatcher = getEventDispatcher();
                        setEventDispatcher(new EventDispatcher() {
                            @Override
                            public Event dispatchEvent(Event event, EventDispatchChain tail) {
                                if (event instanceof MouseEvent) {
                                    if (((MouseEvent) event).getClickCount() == 2) {
                                        if (!event.isConsumed()) {
                                            Requirement requirement = requirementTreeView.getSelectionModel().getSelectedItem().getValue();
                                            RequirementHolder.getInstance().setRequirement(requirement);
                                            Stage stage = ScreenController.getInstance().openNewStage(String.format("requirementFormStage-%d", requirement.getId()));
                                            try {
                                                ScreenController.getInstance().addScene(String.format("requirementFormScene-%d", requirement.getId()), "RequirementForm.fxml");
                                                ScreenController.getInstance().activateScene(String.format("requirementFormScene-%d", requirement.getId()), stage);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        event.consume();
                                    }
                                }
                                return eventDispatcher.dispatchEvent(event, tail);
                            }
                        });
                    } else
                        setText("");
                }

            };
            treeCell.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    String text = treeCell.getText();
                    text = text.replace("\n", " ");
                    treeCell.setText(WordUtils.wrap(text, (int) (70 * (treeCell.getWidth() / 478)), "\n", true));
                }
            });
            return treeCell;
        });
        searchRequirementField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()){
                    case ENTER:
                        searchBtn.fire();
                        event.consume();
                        break;
                    case TAB:
                        searchBtn.requestFocus();
                        event.consume();
                        break;
                }
            }
        });
    }

    private void prepareTreeView() {
        TreeItem<Requirement> rootItem = new TreeItem<>();
        for (Requirement req : requirementObservableList) {
            TreeItem<Requirement> item = new TreeItem<>(req);
            item.getChildren().add(new TreeItem<>());
            item.expandedProperty().addListener(treeViewChangeListener);
            rootItem.getChildren().add(item);
        }
        requirementTreeView.setRoot(rootItem);
        requirementTreeView.setShowRoot(false);
    }

    private void refreshTreeItem(TreeItem<Requirement> root) {
        List<TreeItem<Requirement>> removeList = new ArrayList<>();
        List<Requirement> children;
        if (root.getValue() != null) {
            children = RequirementService.getInstance().getChildrenRequirements(root.getValue());
        } else {
            search();
            children = requirementObservableList;
        }
        for (TreeItem<Requirement> childItem : root.getChildren()) {
            if (children.contains(childItem.getValue())) {
                Requirement child = children.get(children.indexOf(childItem.getValue()));
                children.remove(child);
                childItem.setValue(child);
                if (childItem.isExpanded()) {
                    refreshTreeItem(childItem);
                }
            } else {
                removeList.add(childItem);
            }
        }
        for (TreeItem<Requirement> removeItem : removeList) {
            root.getChildren().remove(removeItem);
        }
        for (Requirement child : children) {
            TreeItem<Requirement> newChildItem = new TreeItem<>(child);
            newChildItem.expandedProperty().addListener(treeViewChangeListener);
            newChildItem.getChildren().add(new TreeItem<>());
            root.getChildren().add(newChildItem);
        }
    }

    public void refreshTreeView() {
        refreshTreeItem(requirementTreeView.getRoot());
    }

    @FXML
    public void onNewRequirementClick(ActionEvent event) throws IOException {
        ObservableList<TreeItem<Requirement>> parentItems = requirementTreeView.getSelectionModel().getSelectedItems();
        List<Requirement> parents = new ArrayList<>();
        parentItems.forEach(parentItem -> {
            parents.add(parentItem.getValue());
        });
        Requirement newRequirement = new Requirement();
        newRequirement.setParents(parents);
        RequirementHolder.getInstance().setRequirement(newRequirement);
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
        search();
        prepareTreeView();
    }

    private void search() {
        requirementObservableList.clear();
        if (searchRequirementField.getText().trim().isEmpty()) {
            List<Requirement> requirements = RequirementService.getInstance()
                    .getRequirements(ProjectHolder.getInstance().getProject());
            for (Requirement requirement : requirements) {
                if (requirement.getLevel() == 1) {
                    requirementObservableList.add(requirement);
                }
            }
        } else {
            List<Requirement> requirements = RequirementService.getInstance()
                    .searchRequirement(searchRequirementField.getText().trim(), ProjectHolder.getInstance().getProject());
            requirementObservableList.addAll(requirements);

        }
    }
}
