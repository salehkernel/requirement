package ir.salmanian.controllers;

import ir.salmanian.services.UserService;
import ir.salmanian.utils.UserHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class LoginController {
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

    public void onRegisterPageClick() throws IOException {
        ScreenController.getInstance().addScreen("register", "../ui/Register.fxml");
        ScreenController.getInstance().activate("register");
    }

    public void onLoginClick() throws IOException {
        if (!checkEmptyFields() && login()) {
            UserHolder.getInstance().setUser(UserService.getInstance().getUserByUsername(usernameField.getText()));
            ScreenController.getInstance().addScreen("projects", "../ui/Projects.fxml");
            ScreenController.getInstance().activate("projects");
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
