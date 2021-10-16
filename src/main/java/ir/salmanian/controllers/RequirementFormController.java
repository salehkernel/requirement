package ir.salmanian.controllers;

import ir.salmanian.models.*;
import ir.salmanian.services.RequirementService;
import ir.salmanian.utils.ProjectHolder;
import ir.salmanian.utils.RequirementHolder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.lang.WordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class RequirementFormController implements Initializable {
    @FXML
    private ComboBox<String> levelComboBox;
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<Priority> priorityCombo;
    @FXML
    private ComboBox<RequirementType> requirementTypeCombo;
    @FXML
    private ComboBox<Change> changesCombo;
    @FXML
    private ComboBox<ReviewStatus> reviewStatusCombo;
    @FXML
    private ComboBox<EvaluationStatus> evaluationStatusCombo;
    @FXML
    private ComboBox<EvaluationMethod> evaluationMethodCombo;
    @FXML
    private ComboBox<QualityFactor> qualityFactorCombo;
    @FXML
    private TextArea attachmentsArea;
    @FXML
    private Button selectParentBtn;
    @FXML
    private ListView<Requirement> parentRequirementListView;
    @FXML
    private ListView<Requirement> childrenRequirementListView;
    @FXML
    private Button saveBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button cancelBtn;
    private ObservableList<Change> changesObservableList;
    private ObservableList<Priority> priorityObservableList;
    private ObservableList<RequirementType> requirementTypeObservableList;
    private ObservableList<ReviewStatus> reviewStatusObservableList;
    private ObservableList<EvaluationStatus> evaluationStatusObservableList;
    private ObservableList<EvaluationMethod> evaluationMethodObservableList;
    private ObservableList<QualityFactor> qualityFactorObservableList;
    private ObservableList<Requirement> parentRequirementObservableList;
    private ObservableList<Requirement> childrenRequirementObservableList;
    private ObservableList<String> levelObservableList;
    private Requirement requirementHolder;

    public RequirementFormController() {
        requirementHolder = RequirementHolder.getInstance().getRequirement();
        Integer maxLevel = RequirementService.getInstance().getMaxLevel(ProjectHolder.getInstance().getProject());
        levelObservableList = FXCollections.observableArrayList();
        if (maxLevel == null)
            maxLevel = 0;
        for (int i = 0; i < maxLevel + 1; i++) {
            String prefix = "";
            for (int j = i; j < maxLevel; j++) {
                prefix += "S";
            }
            prefix += "R";
            levelObservableList.add(prefix);
        }
        Collections.reverse(levelObservableList);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        levelComboBox.setItems(levelObservableList);
        levelComboBox.getSelectionModel().select(0);

        changesObservableList = FXCollections.observableArrayList(Change.values());
        changesCombo.setItems(changesObservableList);
        changesCombo.getSelectionModel().select(0);

        priorityObservableList = FXCollections.observableArrayList(Priority.values());
        priorityCombo.setItems(priorityObservableList);
        priorityCombo.getSelectionModel().select(1);

        requirementTypeObservableList = FXCollections.observableArrayList(RequirementType.values());
        requirementTypeCombo.setItems(requirementTypeObservableList);
        requirementTypeCombo.getSelectionModel().select(0);

        reviewStatusObservableList = FXCollections.observableArrayList(ReviewStatus.values());
        reviewStatusCombo.setItems(reviewStatusObservableList);
        reviewStatusCombo.getSelectionModel().select(2);

        evaluationStatusObservableList = FXCollections.observableArrayList(EvaluationStatus.values());
        evaluationStatusCombo.setItems(evaluationStatusObservableList);
        evaluationStatusCombo.getSelectionModel().select(2);

        evaluationMethodObservableList = FXCollections.observableArrayList(EvaluationMethod.values());
        evaluationMethodCombo.setItems(evaluationMethodObservableList);
        evaluationMethodCombo.getSelectionModel().select(3);

        qualityFactorObservableList = FXCollections.observableArrayList(QualityFactor.values());
        qualityFactorObservableList.add(0, null);
        qualityFactorCombo.setItems(qualityFactorObservableList);

        parentRequirementObservableList = FXCollections.observableArrayList();
        parentRequirementObservableList.addAll(requirementHolder.getParents());
        parentRequirementListView.setItems(parentRequirementObservableList);

        parentRequirementListView.setCellFactory(param -> {
            ListCell<Requirement> listCell = new ListCell<Requirement>() {
                @Override
                protected void updateItem(Requirement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setText(item.toString());
                    } else
                        setText("");
                }
            };
            listCell.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    String text = listCell.getText();
                    text = text.replace("\n", " ");
                    listCell.setText(WordUtils.wrap(text, (int) (70 * (listCell.getWidth() / 478)), "\n", false));
                }
            });
            return listCell;
        });
        childrenRequirementObservableList = FXCollections.observableArrayList();
        List<Requirement> requirementList = RequirementService.getInstance().getChildrenRequirements(requirementHolder);
        childrenRequirementObservableList.addAll(requirementList);
        childrenRequirementListView.setItems(childrenRequirementObservableList);
        childrenRequirementListView.setCellFactory(param -> {
            ListCell<Requirement> listCell = new ListCell<Requirement>() {
                @Override
                protected void updateItem(Requirement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setText(item.toString());
                    } else
                        setText("");
                }
            };
            listCell.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    String text = listCell.getText();
                    text = text.replace("\n", " ");
                    listCell.setText(WordUtils.wrap(text, (int) (70 * (listCell.getWidth() / 478)), "\n", false));
                }
            });
            return listCell;
        });
        if (requirementHolder.getId() == null) {
            cancelBtn.setVisible(true);
            deleteBtn.setVisible(false);

        } else {
            cancelBtn.setVisible(false);
            deleteBtn.setVisible(true);

        }
        levelComboBox.setValue(requirementHolder.getPrefix() == null ? levelComboBox.getItems().get(0) : requirementHolder.getPrefix());
        titleField.setText(requirementHolder.getTitle());
        priorityCombo.setValue(requirementHolder.getPriority() == null ?  priorityCombo.getSelectionModel().getSelectedItem():requirementHolder.getPriority());
        requirementTypeCombo.setValue(requirementHolder.getRequirementType() == null ? requirementTypeCombo.getSelectionModel().getSelectedItem(): requirementHolder.getRequirementType());
        changesCombo.setValue(requirementHolder.getChanges() == null ? changesCombo.getSelectionModel().getSelectedItem(): requirementHolder.getChanges());
        reviewStatusCombo.setValue(requirementHolder.getReviewStatus() == null ? reviewStatusCombo.getSelectionModel().getSelectedItem(): requirementHolder.getReviewStatus());
        evaluationStatusCombo.setValue(requirementHolder.getEvaluationStatus() == null ? evaluationStatusCombo.getSelectionModel().getSelectedItem(): requirementHolder.getEvaluationStatus());
        evaluationMethodCombo.setValue(requirementHolder.getEvaluationMethod() == null ? evaluationMethodCombo.getSelectionModel().getSelectedItem(): requirementHolder.getEvaluationMethod());
        qualityFactorCombo.setValue(requirementHolder.getQualityFactor());
        attachmentsArea.setText(requirementHolder.getAttachment());
    }

    @FXML
    public void onSelectParentClick(ActionEvent event) throws IOException {
        saveTemporalRequirement(requirementHolder);
        RequirementHolder.getInstance().setRequirement(requirementHolder);
        if (requirementHolder.getId() == null) {
            Stage stage = ScreenController.getInstance().openNewStage("newSelectParentStage");
            ScreenController.getInstance().addScene("newSelectParentScene", "SelectParent.fxml");
            ScreenController.getInstance().activateScene("newSelectParentScene", stage);
        } else {
            Stage stage = ScreenController.getInstance().openNewStage(String.format("selectParentStage-%d", requirementHolder.getId()));
            ScreenController.getInstance().addScene(String.format("selectParentScene-%d", requirementHolder.getId()), "SelectParent.fxml");
            ScreenController.getInstance().activateScene(String.format("selectParentScene-%d", requirementHolder.getId()), stage);
        }
    }

    @FXML
    public void onDeleteClick() throws IOException {
        String stageKey = String.format("requirementFormStage-%d", requirementHolder.getId());
        RequirementService.getInstance().deleteRequirement(requirementHolder);
        ScreenController.getInstance().addScene("requirementsScene", "Requirements.fxml");
        ScreenController.getInstance().activateScene("requirementsScene", ScreenController.getInstance().getMainStage());
        ScreenController.getInstance().closeStage(stageKey);
    }

    @FXML
    public void onCancelClick() throws IOException {
        ScreenController.getInstance().closeStage("newRequirementFormStage");
    }

    @FXML
    public void onSaveRequirementClick(ActionEvent event) throws IOException {
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            titleField.setText("");
            titleField.setPromptText("عنوان نیازمندی نمی تواند خالی باشد.");
            titleField.setStyle("-fx-prompt-text-fill: red;");
            return;
        }
        if (levelComboBox.getSelectionModel().getSelectedIndex() != 0 && parentRequirementObservableList.isEmpty()) {
            ButtonType btnType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            Dialog<String> dialog = new Dialog<>();
            dialog.setContentText("لطفا برای این زیر نیازمندی حداقل یک والد انتخاب کنید.");
            dialog.getDialogPane().getButtonTypes().add(btnType);
            dialog.showAndWait();
            return;
        }
        saveTemporalRequirement(requirementHolder);
        String stageKey = "";
        if (requirementHolder.getId() == null) {
            stageKey = "newRequirementFormStage";
        } else {
            stageKey = String.format("requirementFormStage-%d", requirementHolder.getId());
        }
        RequirementService.getInstance().saveRequirement(requirementHolder);
        ScreenController.getInstance().addScene("requirementsScene", "Requirements.fxml");
        ScreenController.getInstance().activateScene("requirementsScene", ScreenController.getInstance().getMainStage());
        ScreenController.getInstance().closeStage(stageKey);
    }

    private void saveTemporalRequirement(Requirement requirement) {

        requirement.setLevel(levelComboBox.getValue().length())
                .setPrefix(levelComboBox.getValue())
                .setTitle(titleField.getText())
                .setPriority(priorityCombo.getValue())
                .setRequirementType(requirementTypeCombo.getValue())
                .setChanges(changesCombo.getValue())
                .setReviewStatus(reviewStatusCombo.getValue())
                .setEvaluationStatus(evaluationStatusCombo.getValue())
                .setEvaluationMethod(evaluationMethodCombo.getValue())
                .setQualityFactor(qualityFactorCombo.getValue())
                .setAttachment(attachmentsArea.getText() != null ? attachmentsArea.getText().trim() : "")
                .setParents(parentRequirementListView.getItems())
                .setProject(ProjectHolder.getInstance().getProject());
    }
}
            