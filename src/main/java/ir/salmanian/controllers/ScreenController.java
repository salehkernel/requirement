package ir.salmanian.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
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

/**
 * A controller layer class for handling switching between stages.
 */
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

    /**
     * This method is used to put a scene to sceneMap with intended key
     * @param sceneKey the key for intended scene,
     * @param fxmlFile the fxml file name of intended scene
     */
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

    /**
     * This method is used to open the scene mapped to intended sceneKey in the intended stage.
     * @param sceneKey the intended sceneKey
     * @param stage the intended stage which should show the scene
     */
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

    /**
     * This method is used to create new stage and put it to stageMap with intended stage key if it does no exist
     * or request focus for stage mapped to the intended stageKey if it is exist
     * @param stageKey the intended stageKey
     * @return the new or existing stage
     */
    public Stage openNewStage(String stageKey) {
        if (stageMap.get(stageKey) != null) {
            stageMap.get(stageKey).requestFocus();
            return stageMap.get(stageKey);
        }
        Stage stage = new Stage();
        stage.setTitle("???????????? ?????????????????????? ????");
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

    /**
     * This method is used to close the stage mapped to intended stageKey and remove it from stageMap
     * @param stageKey the intended stageKey
     */
    public void closeStage(String stageKey) {
        stageMap.get(stageKey).close();
        stageMap.remove(stageKey);
    }

    /**
     * This method is used to get the number of opened stages.
     * @return the number of opened stages.
     */
    public int getOpenStagesCount() {
        return stageMap.size();
    }

    /**
     * This method is used to open a dialog and message that another stage is open which should be closed.
     */
    public void showReturnDialog() {
        final String okBtnText = "??????????";
        final String dialogText = "?????????? ?????????? ?????? ?????? ???????? ???? ???? ????????????.";
        ButtonType ok = new ButtonType(okBtnText, ButtonBar.ButtonData.OK_DONE);
        Dialog returnDialog = new Dialog();
        returnDialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        returnDialog.setContentText(dialogText);
        returnDialog.getDialogPane().getButtonTypes().add(ok);
        returnDialog.showAndWait();
    }

    /**
     * This method is used to show a dialog which message is confirmation of exiting and closing the application.
     */
    private void showExitDialog() {
        final String okBtnText = "????????";
        final String cancelBtnText = "????????????";
        final String dialogText = "?????? ???? ???????????? ???? ???????????? ???????? ??????????";
        ButtonType ok = new ButtonType(okBtnText, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(cancelBtnText, ButtonBar.ButtonData.CANCEL_CLOSE);
        Dialog exitDialog = new Dialog();
        exitDialog.getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        exitDialog.setContentText(dialogText);
        exitDialog.getDialogPane().getButtonTypes().add(ok);
        exitDialog.getDialogPane().getButtonTypes().add(cancel);
        Optional<ButtonType> result = exitDialog.showAndWait();
        if (result.isPresent()) {
            if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE)
                System.exit(0);
        }
    }

    /**
     * This method is used to check if the intended scene has cancel button
     * @param scene the intended scene
     * @return true if th input scene has cancel button, false otherwise.
     */
    private boolean sceneHasCancelBtn(Scene scene) {
        return scene.getRoot().getChildrenUnmodifiable().filtered(node -> node instanceof Button)
                .filtered(node -> ((Button) node).isCancelButton()).size() > 0;
    }
}
