package ir.salmanian.controllers;

import ir.salmanian.models.Requirement;
import ir.salmanian.utils.RequirementHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * The controller class for Templates.fxml file which is used for showing defined templates
 * and selecting one of them.
 * The defined elements ids in fxml file are used in this class.
 * The elements on action method names in fxml file are defined and implemented in this class.
 */
public class TemplatesController implements Initializable {
    private final InputStreamReader templateInputStreamReader;
    @FXML
    private ListView<Pane> templatesListView;
    private Requirement requirementHolder;
    private ObservableList<Pane> paneObservableList = FXCollections.observableArrayList();

    public TemplatesController() {
        requirementHolder = RequirementHolder.getInstance().getRequirement();
        templateInputStreamReader = new InputStreamReader(getClass().getResourceAsStream("/templates.txt"), StandardCharsets.UTF_8);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        templatesListView.setItems(paneObservableList);
        templatesListView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                templatesListView.getParent().fireEvent(event);
            }
        });
    }

    /**
     * This method is used to parse templates file and add a pane of labels and inputs
     * to the paneObservableList for each line
     */
    private void parse() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(templateInputStreamReader);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String text = "";
                FlowPane pane = new FlowPane();
                pane.setHgap(10);
                pane.setVgap(10);
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) != '<' && line.charAt(i) != '>') {
                        text += line.charAt(i);
                    }
                    if (line.charAt(i) == '<' || i == line.length() - 1) {
                        addLabel(pane, text);
                        text = "";

                    }
                    if (line.charAt(i) == '>') {
                        addTextField(pane, text);
                        text = "";
                    }
                }
                addSelectButton(pane);
                paneObservableList.add(pane);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is used to create a label with intended text and add it to intended pane
     * @param pane the intended pane which the label should be added to its children
     * @param text the intended text to be set to created label.
     */
    private void addLabel(Pane pane, String text) {
        if (text.trim().isEmpty())
            return;
        Label label = new Label(text.trim());
        pane.getChildren().add(label);
    }

    /**
     * This method is used to create a text field with intended prompt text and add it to intended pane
     * @param pane the intended pane which the text field should be added to its children
     * @param prompt the intended prompt text to be set to created text field.
     */
    private void addTextField(Pane pane, String prompt) {
        if (prompt.trim().isEmpty())
            return;
        TextField textField = new TextField();
        textField.setPromptText(prompt.trim());
        pane.getChildren().add(textField);
    }

    /**
     * This method is used to create a button and add it to the children of intended pane.
     * The onAction event handler of this button gets the text of intended pane children
     * (except the created button) and use them as title of selected requirements (requirementHolder)
     * @param pane the intended pane which the created button should be added to its children
     */
    private void addSelectButton(Pane pane) {
        Button select = new Button("????????????");
        select.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String title = "";
                for (int i = 0; i < pane.getChildren().size() - 1; i++) {
                    if (pane.getChildren().get(i) instanceof Label) {
                        title += ((Label) pane.getChildren().get(i)).getText() + " ";
                    } else {
                        title += ((TextField) pane.getChildren().get(i)).getText() + " ";
                    }
                }
                requirementHolder.setTitle(title);
                RequirementHolder.getInstance().setRequirement(requirementHolder);
                try {
                    if (requirementHolder.getId() == null) {
                        ScreenController.getInstance().addScene("newRequirementFormScene", "RequirementForm.fxml");
                        ScreenController.getInstance().activateScene("newRequirementFormScene", ScreenController.getInstance().getStage("newRequirementFormStage"));
                        ScreenController.getInstance().closeStage("selectTemplateStageNew");
                    } else {
                        ScreenController.getInstance().addScene(String.format("requirementFormScene-%s", requirementHolder.getId()), "RequirementForm.fxml");
                        ScreenController.getInstance().activateScene(
                                String.format("requirementFormScene-%s", requirementHolder.getId()),
                                ScreenController.getInstance().getStage(
                                        String.format("requirementFormStage-%s", requirementHolder.getId())));
                        ScreenController.getInstance().closeStage(String.format("selectTemplateStage-%s", requirementHolder.getId()));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        pane.getChildren().add(select);
    }
}
