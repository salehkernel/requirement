package ir.salmanian.cells;

import ir.salmanian.controllers.ProjectsController;
import ir.salmanian.io.Exporter;
import ir.salmanian.io.WordExporter;
import ir.salmanian.io.XMLExporter;
import ir.salmanian.models.Project;
import ir.salmanian.models.Requirement;
import ir.salmanian.services.ProjectService;
import ir.salmanian.services.RequirementService;
import ir.salmanian.utils.DocumentUtils;
import ir.salmanian.utils.UserHolder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * ProjectCell class is used for showing each Project object in the projectsListview
 * in {@link ProjectsController} which is the controller of Projects.fxml file.
 */
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
    private MenuButton exportMenuBtn;
    @FXML
    private TextField projectNameField;
    @FXML
    private Button cancelBtn;
    private FXMLLoader fxmlLoader;
    private Exporter exporter;

    @Override
    protected void updateItem(Project project, boolean empty) {
        super.updateItem(project, empty);

        if (empty || project == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/ui/ProjectCell.fxml"));
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
                projectNameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case ENTER:
                                saveProjectBtn.fire();
                                event.consume();
                                break;
                            case ESCAPE:
                                cancelBtn.fire();
                                event.consume();
                                break;
                        }
                    }
                });
                setButtonDefaultKeyEventHandler(saveProjectBtn);
                setButtonDefaultKeyEventHandler(cancelBtn);
            } else {
                viewProjectPane.setVisible(true);
                editProjectPane.setVisible(false);
                projectLabel.setText(project.getName());
                pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            ProjectsController.openProject(project);
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
                prepareExportMenuBtn(project);
                setButtonDefaultKeyEventHandler(editProjectBtn);
                setButtonDefaultKeyEventHandler(deleteProjectBtn);
            }
            setText(null);
            setGraphic(pane);
        }
    }

    private void prepareExportMenuBtn(Project project) {
        MenuItem wordMenuItem = new MenuItem("ورد");
        MenuItem xmlMenuItem = new MenuItem("xml");
        exportMenuBtn.getItems().addAll(wordMenuItem, xmlMenuItem);
        wordMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                exporter = new WordExporter(project);
                exporter.exportToFile();
            }
        });
        xmlMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                exporter = new XMLExporter(project);
                exporter.exportToFile();
            }
        });
    }

    private void setButtonDefaultKeyEventHandler(Button button) {
        button.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        button.fire();
                        event.consume();
                        break;
                    case ESCAPE:
                    case UP:
                    case DOWN:
                        button.getParent().requestFocus();
                        break;
                }
            }
        });
    }


}
