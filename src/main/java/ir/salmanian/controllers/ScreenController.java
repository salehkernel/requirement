package ir.salmanian.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScreenController {
    private Map<String, Pane> screenMap = new HashMap<>();
    private Map<String, Stage> stageMap = new HashMap<>();
    private Scene main;
    private static ScreenController instance;

    private ScreenController() {
    }

    public static ScreenController getInstance() {
        if (instance == null)
            instance = new ScreenController();
        return instance;
    }

    public ScreenController(Scene main) {
        this.main = main;
    }

    public void setMainScene(Scene scene) {
        main = scene;
    }

    public void addScreen(String name, String fxmlFile) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource(fxmlFile));
        screenMap.put(name, pane);
    }

    public void removeScreen(String name) {
        screenMap.remove(name);
    }

    public void activate(String name) {
        main.setRoot(screenMap.get(name));

    }

    public void openNewWindow(String name) {
        Scene newScene = new Scene(screenMap.get(name));
        Stage newStage = new Stage();
        newStage.setTitle("New Stage");
        newStage.setScene(newScene);
        stageMap.put(name, newStage);
        newStage.show();
    }

    public void closeNewWindow(String name) {
        stageMap.get(name).hide();
        stageMap.remove(name);
    }
}
