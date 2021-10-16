package ir.salmanian.controllers;

import ir.salmanian.services.UserService;
import ir.salmanian.utils.UserHolder;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginBtn;
    @FXML
    private Button registerBtn;
    @FXML
    private Label errorLabel;

    public LoginController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginBtn.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE){
                    loginBtn.fire();
                    event.consume();
                }

            }
        });
        passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER){
                    loginBtn.fire();
                    event.consume();
                }
            }
        });
        registerBtn.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER){
                    registerBtn.fire();
                    event.consume();
                }
            }
        });
    }

    public void onRegisterPageClick() throws IOException {
        ScreenController.getInstance().addScene("registerScene", "Register.fxml");
        ScreenController.getInstance().activateScene("registerScene", ScreenController.getInstance().getMainStage());
    }

    public void onLoginClick() throws IOException {
        if (!checkEmptyFields() && login()) {
            UserHolder.getInstance().setUser(UserService.getInstance().getUserByUsername(usernameField.getText()));
            ScreenController.getInstance().addScene("projectsScene", "Projects.fxml");
            ScreenController.getInstance().activateScene("projectsScene", ScreenController.getInstance().getMainStage());
        }
    }

    private boolean login() {
        switch (UserService.getInstance().login(usernameField.getText(), passwordField.getText())) {
            case 0:
                errorLabel.setText("نام کاربری وجود ندارد.");
                return false;
            case 1:
                return true;
            case -1:
                errorLabel.setText("نام کاربری با رمز عبور مطابقت ندارد.");
                return false;
        }
        return false;
    }

    private boolean checkEmptyFields() {
        String message = "";
        if (usernameField.getText().isEmpty()) {
            message = "نام کاربری نمی تواند خالی باشد.";
            errorLabel.setText(message);
            return true;
        } else if (passwordField.getText().isEmpty()) {
            message = "رمز عبور نمی تواند خالی باشد.";
            errorLabel.setText(message);
            return true;

        }
        errorLabel.setText(message);
        return false;
    }
}
