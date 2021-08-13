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
        Stage stage = ScreenController.getInstance().openNewStage("mainStage");
        ScreenController.getInstance().setMainStage(stage);
        ScreenController.getInstance().activateScene("loginScene", stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
