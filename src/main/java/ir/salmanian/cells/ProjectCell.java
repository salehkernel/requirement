package ir.salmanian.cells;

import ir.salmanian.RequirementManagementApplication;
import ir.salmanian.controllers.ScreenController;
import ir.salmanian.models.Project;
import ir.salmanian.models.Requirement;
import ir.salmanian.services.ProjectService;
import ir.salmanian.services.RequirementService;
import ir.salmanian.utils.DocumentUtils;
import ir.salmanian.utils.ProjectHolder;
import ir.salmanian.utils.UserHolder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ProjectCell extends ListCell<Project> {
    @FXML
    private Label projectLabel;
    @FXML
    private Button editProjectBtn;
    @FXML
    private Button deleteProjectBtn;
    @FXML
    private AnchorPane pane;
    @FXML
    private Pane editProjectPane;
    @FXML
    private Pane viewProjectPane;
    @FXML
    private Button saveProjectBtn;
    @FXML
    private Button exportBtn;
    @FXML
    private TextField projectNameField;
    @FXML
    private Button cancelBtn;
    private FXMLLoader fxmlLoader;

    @Override
    protected void updateItem(Project project, boolean empty) {
        super.updateItem(project, empty);

        if (empty || project == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource("../ui/ProjectCell.fxml"));
                fxmlLoader.setController(this);

                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (project.isEditable()) {
                viewProjectPane.setVisible(false);
                editProjectPane.setVisible(true);
                projectNameField.setText(project.getName());
                saveProjectBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (projectNameField.getText() == null || projectNameField.getText().trim().isEmpty()) {
                            projectNameField.setText("");
                            projectNameField.setStyle("-fx-prompt-text-fill: red;");
                            projectNameField.setPromptText("نام پروژه نمی تواند خالی باشد!");
                        } else {
                            project.setName(projectNameField.getText().trim());
                            project.setCreator(UserHolder.getInstance().getUser());
                            project.setEditable(false);
                            ProjectService.getInstance().saveProject(project);
                            updateItem(project, false);
                        }
                    }
                });
                cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (project.getId() == null)
                            getListView().getItems().remove(project);
                        project.setEditable(false);
                        updateItem(project, false);
                    }
                });
            } else {
                viewProjectPane.setVisible(true);
                editProjectPane.setVisible(false);
                projectLabel.setText(project.getName());
                pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            try {
                                ProjectHolder.getInstance().setProject(project);
                                ScreenController.getInstance().addScreen("requirements", "../ui/Requirements.fxml");
                                ScreenController.getInstance().activate("requirements");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                exportBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        List<Requirement> requirementList = RequirementService.getInstance().getRequirements(project);
                        Set<Requirement> requirementParentSet = new HashSet<>();
                        Set<Requirement> requirementChildrenSet = new HashSet<>();
                        try {
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setInitialFileName(String.format("%s.docx", project.getName()));
                            fileChooser.setTitle("Export requirement word");
                            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Word Documents(.docx)", "*.docx"));
                            Stage stage = new Stage();
                            File file = fileChooser.showSaveDialog(stage);

                            if (file != null) {
                                if (!file.getName().endsWith(".docx")) {
                                    file = new File(file.getAbsolutePath() + ".docx");
                                }
                                XWPFDocument document = new XWPFDocument();
                                FileOutputStream outputStream = new FileOutputStream(file);
                                XWPFParagraph paragraph = document.createParagraph();
                                XWPFRun run = paragraph.createRun();
                                run.setBold(true);
                                run.setFontFamily("IRnazanin");
                                run.setFontSize(20);
                                run.setText(String.format("سند نیازمندی های %s", project.getName()));
                                paragraph.setAlignment(ParagraphAlignment.CENTER);
                                DocumentUtils.setParagraphRTL(paragraph);
                                run.addBreak();
                                Iterator<Requirement> requirementIterator = requirementList.iterator();
                                while (requirementIterator.hasNext()){
                                    Requirement req = requirementIterator.next();
                                    if (req.getParents().isEmpty()){
                                        requirementParentSet.add(req);
                                    }
                                    requirementIterator.remove();
                                }
                                writeToDoc(document, requirementParentSet,1);
                                int level = 2;
                                while (!requirementParentSet.isEmpty()) {
                                    Iterator<Requirement> it = requirementParentSet.iterator();
                                    while (it.hasNext()){
                                        Requirement parent = it.next();
                                        List<Requirement> requirementList1 = RequirementService.getInstance().getChildrenRequirements(parent);
                                        if (!requirementList1.isEmpty())
                                            requirementChildrenSet.addAll(requirementList1);
                                        it.remove();
                                    }

                                    writeToDoc(document, requirementChildrenSet,level++);
                                    requirementParentSet = requirementChildrenSet;
                                    requirementChildrenSet = new HashSet<>();
                                }
                                DocumentUtils.setDocumentFont(document,"Lateef");
                                document.write(outputStream);
                                outputStream.close();
                                System.out.println("done");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                editProjectBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        project.setEditable(true);
                        updateItem(project, false);
                    }
                });
                deleteProjectBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ProjectService.getInstance().deleteProject(project);
                        getListView().getItems().remove(project);
                    }
                });
            }
            setText(null);
            setGraphic(pane);
        }
    }

    private void writeToDoc(XWPFDocument document, Set<Requirement> requirements, int level) {
        if (requirements.size() != 0){
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            paragraph.setAlignment(ParagraphAlignment.START);
            DocumentUtils.setParagraphRTL(paragraph);
            run.setText(String.format("نیازمندی‌های سطح %d", level));
            run.addBreak();
            for (Requirement requirement : requirements) {
                DocumentUtils.setParagraphNotSplit(paragraph);
                XWPFTable table = document.createTable(5,4);
                table.setTableAlignment(TableRowAlign.RIGHT);
                XWPFTableRow row1 = table.getRow(0);
                row1.getCell(0).setText(String.format("شماره: %s-%s", requirement.getPrefix(), requirement.getId()));
                row1.getCell(1).setText(String.format("عنوان: %s", requirement.getTitle()));
                row1.setCantSplitRow(true);
                XWPFTableRow row2 = table.getRow(1);
                row2.getCell(0).setText(String.format("اولویت: %s", requirement.getPriority() == null ? "" : requirement.getPriority().label));
                row2.getCell(1).setText(String.format("نوع نیازمندی: %s", requirement.getRequirementType() == null ? "" : requirement.getRequirementType().label));
                row2.getCell(2).setText(String.format("فاکتور کیفی: %s", requirement.getQualityFactor() == null ? "" : requirement.getQualityFactor().toString().toLowerCase()));
                row2.getCell(3).setText(String.format("تغییرات: %s", requirement.getChanges() == null ? "" : requirement.getChanges().label));
                row2.setCantSplitRow(true);
                XWPFTableRow row3 = table.getRow(2);
                row3.getCell(0).setText(String.format("وضعیت بازبینی: %s", requirement.getReviewStatus() == null ? "" : requirement.getReviewStatus().label));
                row3.getCell(1).setText(String.format("روش ارزیابی: %s", requirement.getEvaluationMethod() == null ? "" : requirement.getEvaluationMethod().label));
                row3.getCell(2).setText(String.format("وضعیت ارزیابی: %s", requirement.getEvaluationStatus() == null ? "" : requirement.getEvaluationStatus().label));
                String parentsNumbers = "";
                for (Requirement parent : requirement.getParents()) {
                    parentsNumbers += String.format("%s-%s, ", parent.getPrefix(), parent.getId());
                }
                String childrenNumbers = "";
                for (Requirement child : RequirementService.getInstance().getChildrenRequirements(requirement)) {
                    childrenNumbers += String.format("%s-%s, ", child.getPrefix(), child.getId());
                }
                row3.setCantSplitRow(true);
                XWPFTableRow row4 = table.getRow(3);
                row4.getCell(0).setText(String.format("نیازهای والد: %s", parentsNumbers));
                row4.getCell(1).setText("");
                row4.getCell(2).setText(String.format("نیازهای فرزند: %s", childrenNumbers));
                row4.setCantSplitRow(true);
                XWPFTableRow row5 = table.getRow(4);
                row5.getCell(0).setText(String.format("پیوست: %s", requirement.getAttachment()));
                row5.setCantSplitRow(true);
                DocumentUtils.mergeHorizontally(table, 0, 1, 3);
                DocumentUtils.mergeHorizontally(table, 2, 2, 3);
                DocumentUtils.mergeHorizontally(table, 3, 0, 1);
                DocumentUtils.mergeHorizontally(table, 3, 2, 3);
                DocumentUtils.mergeHorizontally(table, 4, 0, 3);
                DocumentUtils.setTableR2L(table);

                paragraph = document.createParagraph();
                run = paragraph.createRun();
                run.addBreak();
            }
            paragraph = document.createParagraph();
            run = paragraph.createRun();
            run.addBreak();

        }

    }
}
