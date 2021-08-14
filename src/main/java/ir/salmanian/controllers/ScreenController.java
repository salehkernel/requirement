package ir.salmanian.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScreenController {
    private Map<String, Stage> stageMap = new HashMap<>();
    private Map<String, Scene> sceneMap = new HashMap<>();
    private Stage mainStage;
    private static ScreenController instance;

    private ScreenController() {
    }

    public static ScreenController getInstance() {
        if (instance == null)
            instance = new ScreenController();
        return instance;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void addScene(String sceneKey, String fxmlFile) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource(String.format("/ui/%s", fxmlFile)));
        Scene scene = new Scene(pane);
        sceneMap.put(sceneKey, scene);
    }

    public void activateScene(String sceneKey, Stage stage) {
        stage.setScene(sceneMap.get(sceneKey));
        stage.setWidth(this.mainStage.getWidth());
        stage.setHeight(this.mainStage.getHeight());
    }

    public Stage openNewStage(String stageKey) {
        if (stageMap.get(stageKey) != null) {
            stageMap.get(stageKey).requestFocus();
            return stageMap.get(stageKey);
        }
        Stage stage = new Stage();
        stage.setTitle("مدیریت نیازمندی‌‌ ها");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stage.close();
                stageMap.remove(stageKey);
            }
        });
        stageMap.put(stageKey, stage);
        stage.show();
        return stage;
    }

    public Stage getStage(String stageKey) {
        return stageMap.get(stageKey);
    }

    public void closeStage(String stageKey) {
        stageMap.get(stageKey).close();
        stageMap.remove(stageKey);
    }
}
