package ir.salmanian.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ScreenController {
    private final static int MIN_WIDTH = 800;
    private final static int MIN_HEIGHT = 600;
    private static ScreenController instance;
    private Map<String, Stage> stageMap = new HashMap<>();
    private Map<String, Scene> sceneMap = new HashMap<>();
    private Stage mainStage;
    private EventHandler<WindowEvent> closeEventHandler;

    private ScreenController() {
        closeEventHandler = new EventHandler<WindowEvent>() {

            private static final String OK_BTN_TEXT = "خروج";
            private static final String CANCEL_BTN_TEXT = "انصراف";
            private static final String DIALOG_TEXT = "آیا می خواهید از برنامه خارج شوید؟";

            @Override
            public void handle(WindowEvent event) {
                showExitDialog();
                event.consume();
            }

            private void showExitDialog() {
                ButtonType ok = new ButtonType(OK_BTN_TEXT, ButtonBar.ButtonData.OK_DONE);
                ButtonType cancel = new ButtonType(CANCEL_BTN_TEXT, ButtonBar.ButtonData.CANCEL_CLOSE);
                Dialog exitDialog = new Dialog();
                exitDialog.setContentText(DIALOG_TEXT);
                exitDialog.getDialogPane().getButtonTypes().add(ok);
                exitDialog.getDialogPane().getButtonTypes().add(cancel);
                Optional<ButtonType> result = exitDialog.showAndWait();
                if (result.isPresent()) {
                    if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE)
                        System.exit(0);
                }
            }
        };
    }

    public static ScreenController getInstance() {
        if (instance == null)
            instance = new ScreenController();
        return instance;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        mainStage.setOnCloseRequest(closeEventHandler);
        this.mainStage = mainStage;

    }

    public void addScene(String sceneKey, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(String.format("/ui/%s", fxmlFile)));
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        scene.setUserData(loader);
        sceneMap.put(sceneKey, scene);
    }

    public void activateScene(String sceneKey, Stage stage) {
        Scene scene = sceneMap.get(sceneKey);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            }
        });
        stage.setScene(scene);
        stage.setWidth(this.mainStage.getMinWidth());
        stage.setHeight(this.mainStage.getMinHeight());
    }

    public Stage openNewStage(String stageKey) {
        if (stageMap.get(stageKey) != null) {
            stageMap.get(stageKey).requestFocus();
            return stageMap.get(stageKey);
        }
        Stage stage = new Stage();
        stage.setTitle("مدیریت نیازمندی‌‌ ها");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMaximized(false);
        stage.centerOnScreen();
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
