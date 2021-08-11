package ir.salmanian.controllers;

import ir.salmanian.models.*;
import ir.salmanian.services.RequirementService;
import ir.salmanian.utils.ProjectHolder;
import ir.salmanian.utils.RequirementHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RequirementFormController implements Initializable {
    @FXML
    private TextField prefixField;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changesObservableList = FXCollections.observableArrayList(Change.values());
        changesObservableList.add(0, null);
        changesCombo.setItems(changesObservableList);

        priorityObservableList = FXCollections.observableArrayList(Priority.values());
        priorityObservableList.add(0, null);
        priorityCombo.setItems(priorityObservableList);

        requirementTypeObservableList = FXCollections.observableArrayList(RequirementType.values());
        requirementTypeObservableList.add(0, null);
        requirementTypeCombo.setItems(requirementTypeObservableList);

        reviewStatusObservableList = FXCollections.observableArrayList(ReviewStatus.values());
        reviewStatusObservableList.add(0, null);
        reviewStatusCombo.setItems(reviewStatusObservableList);

        evaluationStatusObservableList = FXCollections.observableArrayList(EvaluationStatus.values());
        evaluationStatusObservableList.add(0, null);
        evaluationStatusCombo.setItems(evaluationStatusObservableList);

        evaluationMethodObservableList = FXCollections.observableArrayList(EvaluationMethod.values());
        evaluationMethodObservableList.add(0, null);
        evaluationMethodCombo.setItems(evaluationMethodObservableList);

        qualityFactorObservableList = FXCollections.observableArrayList(QualityFactor.values());
        qualityFactorObservableList.add(0, null);
        qualityFactorCombo.setItems(qualityFactorObservableList);

        parentRequirementObservableList = FXCollections.observableArrayList();
        parentRequirementObservableList.addAll(RequirementHolder.getInstance().getRequirement().getParents());
        parentRequirementListView.setItems(parentRequirementObservableList);

        childrenRequirementObservableList = FXCollections.observableArrayList();
        List<Requirement> requirementList = RequirementService.getInstance().getChildrenRequirements(
                RequirementHolder.getInstance().getRequirement());
        childrenRequirementObservableList.addAll(requirementList);
        childrenRequirementListView.setItems(childrenRequirementObservableList);

        Requirement requirement = RequirementHolder.getInstance().getRequirement();
        if (requirement.getId() == null) {
            cancelBtn.setVisible(true);
            deleteBtn.setVisible(false);
        } else {
            cancelBtn.setVisible(false);
            deleteBtn.setVisible(true);
        }
        prefixField.setText(requirement.getPrefix());
        titleField.setText(requirement.getTitle());
        priorityCombo.setValue(requirement.getPriority());
        requirementTypeCombo.setValue(requirement.getRequirementType());
        changesCombo.setValue(requirement.getChanges());
        reviewStatusCombo.setValue(requirement.getReviewStatus());
        evaluationStatusCombo.setValue(requirement.getEvaluationStatus());
        evaluationMethodCombo.setValue(requirement.getEvaluationMethod());
        attachmentsArea.setText(requirement.getAttachment());

    }

    @FXML
    public void onSelectParentClick(ActionEvent event) throws IOException {
        Requirement requirement = RequirementHolder.getInstance().getRequirement();
        saveTemporalRequirement(requirement);
        ScreenController.getInstance().addScreen("selectParent", "../ui/SelectParent.fxml");
        ScreenController.getInstance().activate("selectParent");
    }

    @FXML
    public void onDeleteClick() throws IOException {
        RequirementService.getInstance().deleteRequirement(RequirementHolder.getInstance().getRequirement());
        ScreenController.getInstance().addScreen("requirements", "../ui/Requirements.fxml");
        ScreenController.getInstance().activate("requirements");

    }

    @FXML
    public void onCancelClick() throws IOException {
        RequirementHolder.getInstance().setRequirement(null);
        ScreenController.getInstance().addScreen("requirements", "../ui/Requirements.fxml");
        ScreenController.getInstance().activate("requirements");
    }

    @FXML
    public void onSaveRequirementClick(ActionEvent event) throws IOException {
        if (prefixField.getText() == null || prefixField.getText().trim().isEmpty()) {
            prefixField.setText("");
            prefixField.setPromptText("پیشوند شمارش نمی تواند خالی باشد.");
            prefixField.setStyle("-fx-prompt-text-fill: red;");
            return;
        }
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            titleField.setText("");
            titleField.setPromptText("عنوان نیازمندی نمی تواند خالی باشد.");
            titleField.setStyle("-fx-prompt-text-fill: red;");
            return;
        }
        Requirement requirement = RequirementHolder.getInstance().getRequirement();
        saveTemporalRequirement(requirement);
        RequirementService.getInstance().saveRequirement(requirement);
        ScreenController.getInstance().addScreen("requirements", "../ui/Requirements.fxml");
        ScreenController.getInstance().activate("requirements");
        if (requirement.getId() != null){
            ScreenController.getInstance().closeNewWindow(requirement.getPrefix() + "-" +requirement.getId());
        }
//        ScreenController.getInstance().closeNewWindow("requirementForm");
    }

    private void saveTemporalRequirement(Requirement requirement) {

        requirement.setPrefix(prefixField.getText())
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
            