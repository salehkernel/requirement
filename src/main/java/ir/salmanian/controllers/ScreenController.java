package ir.salmanian.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.*;

public class ScreenController {
    private final static int MIN_WIDTH = 800;
    private final static int MIN_HEIGHT = 600;
    private static ScreenController instance;
    private Map<String, Stage> stageMap = new HashMap<>();
    private Map<String, Scene> sceneMap = new HashMap<>();
    private Stage mainStage;
    private EventHandler<WindowEvent> closeEventHandler;
    private Set<String> mainStageSceneKeys = new LinkedHashSet<>();

    private ScreenController() {
        closeEventHandler = new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                showExitDialog();
                event.consume();
            }
        };
        mainStageSceneKeys.add("loginScene");
        mainStageSceneKeys.add("registerScene");
        mainStageSceneKeys.add("projectsScene");
        mainStageSceneKeys.add("requirementsScene");
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
        Scene scene;
        if (mainStageSceneKeys.contains(sceneKey)) {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getBounds();
            scene = new Scene(pane, bounds.getWidth(), bounds.getHeight());
        } else {
            scene = new Scene(pane);
        }
        scene.setUserData(loader);
        sceneMap.put(sceneKey, scene);
    }

    public void activateScene(String sceneKey, Stage stage) {
        Scene scene = sceneMap.get(sceneKey);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ESCAPE:
                        if (!sceneHasCancelBtn(scene)) {
                            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                        }
                        break;
                    case ENTER:
                        if (scene.getFocusOwner() instanceof Button) {
                            ((Button) scene.getFocusOwner()).fire();
                        } else {
                            if (scene.getFocusOwner().getOnKeyPressed() != null) {
                                scene.getFocusOwner().fireEvent(event);
                            }
                        }
                        break;
                }
            }
        });
        stage.setScene(scene);
        if (mainStageSceneKeys.contains(sceneKey)) {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getBounds();
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        } else {
            stage.setWidth(this.mainStage.getMinWidth());
            stage.setHeight(this.mainStage.getMinHeight());
        }
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

    public int getOpenStagesCount() {
        return stageMap.size();
    }

    public void showReturnDialog() {
        final String okBtnText = "تأیید";
        final String dialogText = "پنجره دیگری باز است لطفا آن را ببندید.";
        ButtonType ok = new ButtonType(okBtnText, ButtonBar.ButtonData.OK_DONE);
        Dialog returnDialog = new Dialog();
        returnDialog.setContentText(dialogText);
        returnDialog.getDialogPane().getButtonTypes().add(ok);
        returnDialog.showAndWait();
    }

    private void showExitDialog() {
        final String okBtnText = "خروج";
        final String cancelBtnText = "انصراف";
        final String dialogText = "آیا می خواهید از برنامه خارج شوید؟";
        ButtonType ok = new ButtonType(okBtnText, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(cancelBtnText, ButtonBar.ButtonData.CANCEL_CLOSE);
        Dialog exitDialog = new Dialog();
        exitDialog.setContentText(dialogText);
        exitDialog.getDialogPane().getButtonTypes().add(ok);
        exitDialog.getDialogPane().getButtonTypes().add(cancel);
        Optional<ButtonType> result = exitDialog.showAndWait();
        if (result.isPresent()) {
            if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE)
                System.exit(0);
        }
    }

    private boolean sceneHasCancelBtn(Scene scene) {
        return scene.getRoot().getChildrenUnmodifiable().filtered(node -> node instanceof Button)
                .filtered(node -> ((Button) node).isCancelButton()).size() > 0;
    }
}
