package ir.salmanian.controllers;

import ir.salmanian.cells.FilterTreeCell;
import ir.salmanian.cells.RequirementTreeCell;
import ir.salmanian.filter.Filter;
import ir.salmanian.filter.FilterItem;
import ir.salmanian.filter.FilterName;
import ir.salmanian.models.*;
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
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.commons.lang.WordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class RequirementsController implements Initializable {

    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private final ClipboardContent clipboardContent = new ClipboardContent();
    @FXML
    private TextField searchRequirementField;
    @FXML
    private TreeView<Requirement> requirementTreeView;
    @FXML
    private TreeView<Filter<Object>> filterTreeView;
    @FXML
    private Button searchBtn;
    @FXML
    private Button createRequirementBtn;
    private ObservableList<Requirement> requirementObservableList;
    private ObservableList<Filter> filterObservableList;
    private ChangeListener<Boolean> treeViewChangeListener;

    public RequirementsController() {

        loadRequirements();
        loadFilters();
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

        prepareRequirementsTreeView();
        prepareFilterTreeView();
        filterTreeView.setCellFactory(param -> {
            TreeCell<Filter<Object>> cell = new FilterTreeCell();
            return cell;
        });
        requirementTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        requirementTreeView.setCellFactory(param -> {
            TreeCell<Requirement> treeCell = new RequirementTreeCell();

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
        prepareRequirementsTreeView();
    }

    @FXML
    public void onMetRequirementsClick(ActionEvent event) {
        containsMetRequirement(requirementTreeView.getRoot());
    }

    @FXML
    public void onFilterClick(ActionEvent event) {
        System.out.println("filter");
        Set<Requirement> filteredSet = new LinkedHashSet<>();
        boolean emptySelection = true;
        for (Filter<Object> filter : filterObservableList) {
            if (!emptySelection)
                break;
            for (FilterItem<Object> item : filter.getItems()) {
                if (item.isSelected()) {
                    emptySelection = false;
                    break;
                }
            }
        }
        for (Filter<Object> filter : filterObservableList) {
            List<Object> selectedValues = filter.getItems().stream().filter(item -> item.isSelected())
                    .map(item -> item.getItem()).collect(Collectors.toList());
            List<Requirement> filtered = RequirementService.getInstance().filter(filter.getField(), selectedValues);
            if (filtered != null)
                filteredSet.addAll(filtered);
        }
        if (!emptySelection) {
            requirementObservableList.clear();
            requirementObservableList.addAll(filteredSet.stream().sorted((r1, r2) -> {
                if (r1.getLevel() == r2.getLevel())
                    return Integer.compare(r1.getNumber(), r2.getNumber());
                else
                    return Integer.compare(r1.getLevel(), r2.getLevel());
            }).collect(Collectors.toList()));
        } else
            loadRequirements();
        prepareRequirementsTreeView();
    }

    private void loadRequirements() {
        requirementObservableList = FXCollections.observableArrayList();
        List<Requirement> requirements = RequirementService.getInstance()
                .getRequirements(ProjectHolder.getInstance().getProject());
        for (Requirement requirement : requirements) {
            if (requirement.getLevel() == 1) {
                requirementObservableList.add(requirement);
            }
        }
    }

    private void loadFilters() {
        filterObservableList = FXCollections.observableArrayList();
        try {
            Filter<Priority> priorityFilter = new Filter<>(FilterName.PRIORITY,
                    Requirement.class.getDeclaredField("priority"),
                    Arrays.asList(Priority.values()).stream().map(value -> new FilterItem<>(value))
                            .collect(Collectors.toList()));
            Filter<RequirementType> typeFilter = new Filter<>(FilterName.REQUIREMENT_TYPE,
                    Requirement.class.getDeclaredField("requirementType"),
                    Arrays.asList(RequirementType.values()).stream().map(value -> new FilterItem<>(value))
                            .collect(Collectors.toList()));
            Filter<Change> changeFilter = new Filter<>(FilterName.CHANGE,
                    Requirement.class.getDeclaredField("changes"),
                    Arrays.asList(Change.values()).stream().map(value -> new FilterItem<>(value))
                            .collect(Collectors.toList()));
            Filter<ReviewStatus> reviewStatusFilter = new Filter<>(FilterName.REVIEW_STATUS,
                    Requirement.class.getDeclaredField("reviewStatus"),
                    Arrays.asList(ReviewStatus.values()).stream().map(value -> new FilterItem<>(value))
                            .collect(Collectors.toList()));
            Filter<EvaluationStatus> evaluationStatusFilter = new Filter<>(FilterName.EVALUATION_STATUS,
                    Requirement.class.getDeclaredField("evaluationStatus"),
                    Arrays.asList(EvaluationStatus.values()).stream().map(value -> new FilterItem<>(value))
                            .collect(Collectors.toList()));
            Filter<EvaluationMethod> evaluationMethodFilter = new Filter<>(FilterName.EVALUATION_METHOD,
                    Requirement.class.getDeclaredField("evaluationMethod"),
                    Arrays.asList(EvaluationMethod.values()).stream().map(value -> new FilterItem<>(value))
                            .collect(Collectors.toList()));
            Filter<QualityFactor> qualityFactorFilter = new Filter<>(FilterName.QUALITY_FACTOR,
                    Requirement.class.getDeclaredField("qualityFactor"),
                    Arrays.asList(QualityFactor.values()).stream().map(value -> new FilterItem<>(value))
                            .collect(Collectors.toList()));
            filterObservableList.add(priorityFilter);
            filterObservableList.add(typeFilter);
            filterObservableList.add(changeFilter);
            filterObservableList.add(reviewStatusFilter);
            filterObservableList.add(evaluationStatusFilter);
            filterObservableList.add(evaluationMethodFilter);
            filterObservableList.add(qualityFactorFilter);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void prepareRequirementsTreeView() {
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

    private void prepareFilterTreeView() {
        TreeItem<Filter<Object>> rootItem = new TreeItem<>();
        for (Filter filter : filterObservableList) {
            TreeItem<Filter<Object>> parentItem = new TreeItem<>(filter);
            TreeItem<Filter<Object>> childItem = new TreeItem<>(new Filter(null, filter.getField(), filter.getItems()));
            parentItem.getChildren().add(childItem);
            rootItem.getChildren().add(parentItem);
            parentItem.setExpanded(true);
        }
        filterTreeView.setRoot(rootItem);
        filterTreeView.setShowRoot(false);
    }

    public void refreshTreeView() {
        refreshTreeItem(requirementTreeView.getRoot());
    }

    public static void openRequirementForm(Requirement requirement) {
        RequirementHolder.getInstance().setRequirement(requirement);
        Stage stage = ScreenController.getInstance().openNewStage(
                String.format("requirementFormStage-%s", requirement.getId()));
        try {
            ScreenController.getInstance().addScene(
                    String.format("requirementFormScene-%s", requirement.getId()), "RequirementForm.fxml");
            ScreenController.getInstance().activateScene(
                    String.format("requirementFormScene-%s", requirement.getId()), stage);
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
