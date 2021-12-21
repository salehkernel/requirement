package ir.salmanian.controllers;

import ir.salmanian.models.User;
import ir.salmanian.services.UserService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller class for Register.fxml file which is used to register new user.
 * The defined elements ids in fxml file are used in this class.
 * The elements on action method names in fxml file are defined and implemented in this class.
 */
public class RegisterController implements Initializable {
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
    @FXML
    private AnchorPane registerPane;
    private EventHandler<KeyEvent> defaultEnterKeyPressEventHandler;

    public RegisterController() {
        defaultEnterKeyPressEventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER){
                    registerBtn.fire();
                    event.consume();
                }
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String backgroundPath = getClass().getResource("/img/login.jpeg").toExternalForm();
        registerPane.setStyle("-fx-background-image: url(" + backgroundPath + "); -fx-background-size: cover;");
        usernameField.setOnKeyPressed(defaultEnterKeyPressEventHandler);
        passwordField.setOnKeyPressed(defaultEnterKeyPressEventHandler);
        repeatPasswordField.setOnKeyPressed(defaultEnterKeyPressEventHandler);
        emailField.setOnKeyPressed(defaultEnterKeyPressEventHandler);
    }

    @FXML
    public void onRegisterClick() {
        if (!checkEmptyFields() && checkRepeatPassword() && !usernameExists() && !emailExists()) {
            User user = new User();
            user.setUsername(usernameField.getText());
            user.setPassword(passwordField.getText());
            user.setEmail(emailField.getText());
            UserService.getInstance().registerUser(user);
            ScreenController.getInstance().activateScene("loginScene", ScreenController.getInstance().getMainStage());
        }
    }

    @FXML
    public void onBackClick() throws IOException {
        ScreenController.getInstance().activateScene("loginScene", ScreenController.getInstance().getMainStage());
    }

    /**
     * This method is used to chek if the entered password and repeat password are equal,
     * @return true if the password and repeat password are equals, false otherwise.
     */
    private boolean checkRepeatPassword() {
        errorLabel.setText("");
        if (!repeatPasswordField.getText().trim().equals(passwordField.getText().trim())) {
            errorLabel.setText("رمز عبور با تکرار رمز عبور همخوانی ندارد.");
            return false;
        }
        return true;
    }

    /**
     * This method checks register form fields and show proper message if the fields are Empty.
     *
     * @return true if the there is empty field, false otherwise.
     */
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

    /**
     * This method is used to check if the entered username exists in database or not.
     * @return true if the username exists in database, false otherwise.
     */
    private boolean usernameExists() {
        if (UserService.getInstance().usernameExists(usernameField.getText())) {
            errorLabel.setText("نام کاربری وجود دارد.");
            return true;
        }
        return false;
    }

    /**
     * This method is used to check if the entered email address exists in database or not.
     * @return true if the email address exists in database, false otherwise.
     */
    private boolean emailExists() {
        if (UserService.getInstance().emailExists(emailField.getText())) {
            errorLabel.setText("ایمیل وجود دارد.");
            return true;
        }
        return false;
    }
}
