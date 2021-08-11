package ir.salmanian.controllers;

import ir.salmanian.models.User;
import ir.salmanian.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private TextField emailField;
    @FXML
    private Button registerBtn;
    @FXML
    private Label errorLabel;


    public void onRegisterClick() {
        errorLabel.setText("");
        if (!checkEmptyFields() && checkRepeatPassword() && !usernameExists() && !emailExists()) {
            User user = new User();
            user.setUsername(usernameField.getText());
            user.setPassword(passwordField.getText());
            user.setEmail(emailField.getText());
            UserService.getInstance().registerUser(user);
            ScreenController.getInstance().activate("login");
        }

    }

    private boolean checkRepeatPassword() {
        errorLabel.setText("");
        if (!repeatPasswordField.getText().trim().equals(passwordField.getText().trim())) {
            errorLabel.setText("رمز عبور با تکرار رمز عبور همخوانی ندارد.");
            return false;
        }
        return true;
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

        } else if (repeatPasswordField.getText().isEmpty()) {
            message = "تکرار رمز عبور نمی تواند خالی باشد.";
            errorLabel.setText(message);
            return true;
        } else if (emailField.getText().isEmpty()) {
            message = "ایمیل نمی تواند خالی باشد.";
            errorLabel.setText(message);
            return true;
        }
        errorLabel.setText(message);
        return false;
    }

    private boolean usernameExists() {
        if (UserService.getInstance().usernameExists(usernameField.getText())) {
            errorLabel.setText("نام کاربری وجود دارد.");
            return true;
        }
        return false;
    }

    private boolean emailExists() {
        if (UserService.getInstance().emailExists(emailField.getText())) {
            errorLabel.setText("ایمیل وجود دارد.");
            return true;
        }
        return false;
    }
    @FXML
    public void onBackClick() throws IOException {
        ScreenController.getInstance().addScreen("login", "../ui/Login.fxml");
        ScreenController.getInstance().activate("login");
    }
}
