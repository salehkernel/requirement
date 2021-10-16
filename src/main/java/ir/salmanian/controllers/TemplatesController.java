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
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class TemplatesController implements Initializable {
    @FXML
    private ListView<Pane> templatesListView;

    private Requirement requirementHolder;
    private final File templatesFile;
    private ObservableList<Pane> paneObservableList = FXCollections.observableArrayList();

    public TemplatesController() {
        requirementHolder = RequirementHolder.getInstance().getRequirement();
        templatesFile = new File("/media/yasin/Local Disk/workspace/work/Java/Roshd/requirement-management/src/main/resources/templates.txt");
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

    private void parse() throws FileNotFoundException {
        Scanner sc = new Scanner(templatesFile);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
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
    }

    private void addLabel(Pane pane, String text) {
        if (text.trim().isEmpty())
            return;
        Label label = new Label(text.trim());
        pane.getChildren().add(label);
    }

    private void addTextField(Pane pane, String prompt) {
        if (prompt.trim().isEmpty())
            return;
        TextField textField = new TextField();
        textField.setPromptText(prompt.trim());
        pane.getChildren().add(textField);
    }

    private void addSelectButton(Pane pane) {
        Button select = new Button("انتخاب");
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
                    }
                    else {
                        ScreenController.getInstance().addScene(String.format("requirementFormScene-%d", requirementHolder.getId()), "RequirementForm.fxml");
                        ScreenController.getInstance().activateScene(
                                String.format("requirementFormScene-%d", requirementHolder.getId()),
                                ScreenController.getInstance().getStage(
                                        String.format("requirementFormStage-%d", requirementHolder.getId())));
                        ScreenController.getInstance().closeStage(String.format("selectTemplateStage-%d", requirementHolder.getId()));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        pane.getChildren().add(select);
    }
}
