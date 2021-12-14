package ir.salmanian.controllers;

import ir.salmanian.cells.RequirementTreeCell;
import ir.salmanian.models.EvaluationStatus;
import ir.salmanian.models.Requirement;
import ir.salmanian.services.RequirementService;
import ir.salmanian.utils.ProjectHolder;
import ir.salmanian.utils.RequirementHolder;
import javafx.beans.binding.Bindings;
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
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.apache.commons.lang.WordUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RequirementsController implements Initializable {

    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private final ClipboardContent clipboardContent = new ClipboardContent();
    @FXML
    private TextField searchRequirementField;
    @FXML
    private TreeView<Requirement> requirementTreeView;
    @FXML
    private Button searchBtn;
    @FXML
    private Button createRequirementBtn;
    private ObservableList<Requirement> requirementObservableList;
    private ChangeListener<Boolean> treeViewChangeListener;

    public RequirementsController() {

        loadRequirements();
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
        requirementTreeView.setCellFactory(param -> {
            TreeCell<Requirement> treeCell = new RequirementTreeCell();
            /*TreeCell<Requirement> treeCell = new TreeCell<Requirement>() {
                FlowPane pane;
                ImageView requirementIcon;
                Label requirementTitleLabel;

                @Override
                public void updateItem(Requirement item, boolean empty) {
                    super.updateItem(item, empty);
                    pane = new FlowPane(Orientation.HORIZONTAL, 15, 15);
                    requirementIcon = new ImageView();
                    requirementTitleLabel = new Label();
                    if (item != null && !empty) {
                        requirementTitleLabel.setText(item.toString());
                        requirementTitleLabel.setWrapText(true);
                        try {
                            requirementIcon.setImage(new Image(getClass()
                                    .getResource(String.format("/img/%s.png", item.getLevel() == 1 ? "user" : "system"))
                                    .toURI().toString()));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        pane.getChildren().add(requirementIcon);
                        pane.getChildren().add(requirementTitleLabel);
                        requirementTitleLabel.styleProperty().bind(Bindings.createStringBinding(
                                () -> concatAllStyles(getTextFillStyle(item))));
                        styleProperty().bind(Bindings.createStringBinding(
                                () -> concatAllStyles(getBorderColorStyle(item), "-fx-border-radius: 5px;",
                                        getBorderInsetsStyle(item), "-fx-indent: 0px;", "-fx-border-width: 0px 0px 1px 1px;")
                        ));
                        updateTreeItem(getTreeItem());
                        EventDispatcher eventDispatcher = getEventDispatcher();
                        pane.setEventDispatcher(new EventDispatcher() {
                            @Override
                            public Event dispatchEvent(Event event, EventDispatchChain tail) {
                                if (event instanceof MouseEvent) {
                                    if (((MouseEvent) event).getClickCount() == 2) {
                                        if (!event.isConsumed()) {
                                            openRequirementForm(item);
                                        }
                                        event.consume();
                                    }
                                }
                                return eventDispatcher.dispatchEvent(event, tail);
                            }
                        });
                        setGraphic(pane);
                    } else {
                        setText("");
                        setGraphic(null);
                        styleProperty().bind(Bindings.createStringBinding(() -> ""));
                    }
                }

                private String getBorderColorStyle(Requirement requirement) {
                    String styleName = "-fx-border-color: ";
                    String borderColor = getRequirementLevelColor(requirement);
                    return String.format("%s%s;", styleName, borderColor);
                }

                private String getRequirementLevelColor(Requirement requirement) {
                    String borderColor = "";
                    switch (requirement.getLevel() % 10) {
                        case 1:
                            borderColor = "#6DA472";
                            break;
                        case 2:
                            borderColor = "#D2B48C";
                            break;
                        case 3:
                            borderColor = "#0000FF";
                            break;
                        case 4:
                            borderColor = "#2E8B57";
                            break;
                        case 5:
                            borderColor = "#8B4513";
                            break;
                        case 6:
                            borderColor = "#00FFFF";
                            break;
                        case 7:
                            borderColor = "#B0E0E6";
                            break;
                        case 8:
                            borderColor = "#DEB887";
                            break;
                        case 9:
                            borderColor = "#708090";
                            break;
                        case 0:
                            borderColor = "#8A2BE2";
                            break;

                    }
                    return borderColor;
                }

                private String getTextFillStyle(Requirement requirement) {
                    String styleName = "-fx-text-fill: ";
                    String fillColor = requirement.getEvaluationStatus() == EvaluationStatus.MET ? "green" : "black";
                    return String.format("%s%s;", styleName, fillColor);
                }

                private String getBorderInsetsStyle(Requirement requirement) {
                    String styleName = "-fx-border-insets: ";
                    String value = String.format("1px 1px 1px %dpx", (requirement.getLevel() - 1) * 25 + 1);
                    return String.format("%s%s;", styleName, value);
                }

                private String concatAllStyles(String... styles) {
                    StringBuilder allStyles = new StringBuilder("");
                    for (String style : styles) {
                        if (style != null)
                            allStyles.append(style);
                    }
                    return allStyles.toString();
                }
            }*/;
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
        requirementTreeView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ESCAPE:
                        if (!requirementTreeView.getSelectionModel().isEmpty()) {
                            requirementTreeView.getSelectionModel().clearSelection();
                        } else {
                            requirementTreeView.getParent().fireEvent(event);
                        }
                        break;
                    case ENTER:
                        TreeItem<Requirement> selected;
                        if ((selected = requirementTreeView.getSelectionModel().getSelectedItem()) != null) {
                            openRequirementForm(selected.getValue());
                        }
                        break;
                    case C:
                        if (event.isControlDown()) {
                            TreeItem<Requirement> selectedItem = requirementTreeView.getSelectionModel().getSelectedItem();
                            clipboardContent.putString(selectedItem != null ? selectedItem.getValue().getTitle() : clipboardContent.getString());
                            clipboard.setContent(clipboardContent);
                        }
                        break;
                    case V:
                        if (event.isControlDown()) {
                            TreeItem<Requirement> selectedItem = requirementTreeView.getSelectionModel().getSelectedItem();
                            if (selectedItem != null) {
                                selectedItem.getValue().setTitle(clipboard.getString());
                                openRequirementForm(selectedItem.getValue());
                            }
                        }
                        break;
                    case N:
                        if (event.isControlDown())
                            createRequirementBtn.fire();
                        break;
                }
            }
        });
        searchRequirementField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
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
        if (ScreenController.getInstance().getOpenStagesCount() > 1) {
            ScreenController.getInstance().showReturnDialog();
        } else {
            ScreenController.getInstance()
                    .activateScene("projectsScene", ScreenController.getInstance().getMainStage());
        }
    }

    @FXML
    public void onSearchClick(ActionEvent event) {
        search();
        prepareTreeView();
    }

    @FXML
    public void onMetRequirementsClick(ActionEvent event) {
        containsMetRequirement(requirementTreeView.getRoot());
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

    public void refreshTreeView() {
        refreshTreeItem(requirementTreeView.getRoot());
    }

    public static void openRequirementForm(Requirement requirement) {
        RequirementHolder.getInstance().setRequirement(requirement);
        Stage stage = ScreenController.getInstance().openNewStage(
                String.format("requirementFormStage-%d", requirement.getId()));
        try {
            ScreenController.getInstance().addScene(
                    String.format("requirementFormScene-%d", requirement.getId()), "RequirementForm.fxml");
            ScreenController.getInstance().activateScene(
                    String.format("requirementFormScene-%d", requirement.getId()), stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                childItem.setExpanded(childItem.isExpanded() ||
                        requirementTreeView.getSelectionModel().getSelectedItems().contains(childItem));
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

    private boolean containsMetRequirement(TreeItem<Requirement> root) {
        boolean isNowExpanded = root.isExpanded();
        root.setExpanded(true);
        boolean contains = false;
        for (TreeItem<Requirement> child : root.getChildren()) {
            if (containsMetRequirement(child) || child.getValue().getEvaluationStatus() == EvaluationStatus.MET) {
                contains = true;
            }
        }
        root.setExpanded(contains || isNowExpanded);
        return contains;
    }
}
