package ir.salmanian;

import ir.salmanian.controllers.ScreenController;
import ir.salmanian.db.DatabaseManagement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class RequirementManagementApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        DatabaseManagement.getInstance();
        FXMLLoader fxmlLoader = new FXMLLoader();

        Pane loginPane = fxmlLoader.load(getClass().getResource("ui/Login.fxml").openStream());
        primaryStage.setTitle("مدیریت نیازمندی ها");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setMaximized(true);
        Scene root = new Scene(loginPane);
        primaryStage.setScene(root);
        ScreenController.getInstance().setMainScene(root);
        ScreenController.getInstance().addScreen("login", "../ui/Login.fxml");
        ScreenController.getInstance().addScreen("register", "../ui/Register.fxml");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
