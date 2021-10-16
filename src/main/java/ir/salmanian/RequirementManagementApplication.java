package ir.salmanian;

import ir.salmanian.controllers.ScreenController;
import ir.salmanian.db.DatabaseManagement;
import javafx.application.Application;
import javafx.stage.Stage;

public class RequirementManagementApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        DatabaseManagement.getInstance();
        ScreenController.getInstance().addScene("loginScene", "Login.fxml");
        Stage mainStage = ScreenController.getInstance().openNewStage("mainStage");
        mainStage.setMaximized(true);
        ScreenController.getInstance().setMainStage(mainStage);
        ScreenController.getInstance().activateScene("loginScene", mainStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
