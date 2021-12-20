package ir.salmanian.controllers;

import ir.salmanian.db.DBMS;
import ir.salmanian.db.DatabaseConfig;
import ir.salmanian.db.DatabaseManagement;
import ir.salmanian.services.UserService;
import ir.salmanian.utils.UserHolder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller class for Login.fxml file which is used for authenticating application users.
 * The defined elements ids in fxml file are used in this class.
 * The elements on action method names in fxml file are defined and implemented in this class.
 */

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
    @FXML
    private RadioButton embeddedDBRadioBtn;
    @FXML
    private RadioButton externalDBRadioBtn;
    @FXML
    private Button confirmBtn;
    @FXML
    private ComboBox<DBMS> dbTypeCombo;
    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;
    @FXML
    private TextField dbNameField;
    @FXML
    private TextField dbUsernameField;
    @FXML
    private TextField dbPasswordField;
    @FXML
    private Label connectionLabel;
    @FXML
    private Button dbTestBtn;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private AnchorPane dbConnectionPane;

    private ToggleGroup dbToggleGroup;
    private ObservableList<DBMS> dbmsObservableList;
    private EventHandler<KeyEvent> defaultEnterKeyPressEventHandler;
    private EventHandler<KeyEvent> defaultConnectionKeyPressEventHandler;
    private boolean applicationStartup = true;

    private DatabaseConfig dbConfig;

    public LoginController() {
        dbConfig = DatabaseConfig.getInstance();
        defaultEnterKeyPressEventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    loginBtn.fire();
                    event.consume();
                }
            }
        };
        defaultConnectionKeyPressEventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    dbTestBtn.fire();
                    event.consume();
                }
            }
        };
        dbToggleGroup = new ToggleGroup();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String backgroundPath = getClass().getResource("/img/login.jpeg").toExternalForm();
        loginPane.setStyle("-fx-background-image: url(" + backgroundPath + ");-fx-background-size: cover;");

        usernameField.setOnKeyPressed(defaultEnterKeyPressEventHandler);
        passwordField.setOnKeyPressed(defaultEnterKeyPressEventHandler);

        hostField.setOnKeyPressed(defaultConnectionKeyPressEventHandler);
        portField.setOnKeyPressed(defaultConnectionKeyPressEventHandler);
        dbNameField.setOnKeyPressed(defaultConnectionKeyPressEventHandler);
        dbUsernameField.setOnKeyPressed(defaultConnectionKeyPressEventHandler);
        dbPasswordField.setOnKeyPressed(defaultConnectionKeyPressEventHandler);

        embeddedDBRadioBtn.setToggleGroup(dbToggleGroup);
        externalDBRadioBtn.setToggleGroup(dbToggleGroup);
        dbToggleGroup.selectToggle(embeddedDBRadioBtn);
        confirmBtn.fire();

        dbmsObservableList = FXCollections.observableArrayList(DBMS.values());
        dbTypeCombo.setItems(dbmsObservableList);
        dbTypeCombo.getSelectionModel().select(0);

        portField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    portField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        applicationStartup = false;
    }

    @FXML
    public void onRegisterPageClick() throws IOException {
        ScreenController.getInstance().addScene("registerScene", "Register.fxml");
        ScreenController.getInstance().activateScene("registerScene", ScreenController.getInstance().getMainStage());
        errorLabel.setText("");
        connectionLabel.setText("");
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }

    @FXML
    public void onLoginClick() throws IOException {
        if (!checkEmptyFields() && login()) {
            UserHolder.getInstance().setUser(UserService.getInstance().getUserByUsername(usernameField.getText()));
            ScreenController.getInstance().addScene("projectsScene", "Projects.fxml");
            ScreenController.getInstance().activateScene("projectsScene", ScreenController.getInstance().getMainStage());
            errorLabel.setText("");
            connectionLabel.setText("");
            usernameField.clear();
            passwordField.clear();
            usernameField.requestFocus();
        }
    }

    @FXML
    public void onEmbeddedDBClick(ActionEvent event) {
        dbConnectionPane.setVisible(false);
    }

    @FXML
    public void onExternalDBClick(ActionEvent event) {
        dbConnectionPane.setVisible(true);
    }

    @FXML
    public void onDBTestClick(ActionEvent event) {
        if (connectionPaneFieldsAreOK()) {
            DBMS dbms = dbTypeCombo.getValue();
            String hostAddress = hostField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            String dbName = dbNameField.getText().trim();
            String username = dbUsernameField.getText().trim();
            String password = dbPasswordField.getText().trim();
            if (dbConfig.testConnection(dbms, hostAddress, port, dbName, username, password)) {
                connectionLabel.setText("تست موفق");
                connectionLabel.setTextFill(Color.GREEN);
            } else {
                connectionLabel.setText("خطا در برقراری ارتباط");
                connectionLabel.setTextFill(Color.RED);
            }
        }
    }

    @FXML
    public void onConfirmClick(ActionEvent event) {
        RadioButton selected = (RadioButton) dbToggleGroup.getSelectedToggle();
        if (selected.equals(embeddedDBRadioBtn)) {
            DatabaseManagement.getInstance().setSessionFactory(dbConfig.useEmbeddedDB());
            if (!applicationStartup) {
                connectionLabel.setText("پایگاه داده نرم افزار به پایگاه داده تعبیه شده تغییر یافت.");
                connectionLabel.setTextFill(Color.GREEN);
            }
        } else {
            if (connectionPaneFieldsAreOK()) {
                DBMS dbms = dbTypeCombo.getValue();
                String hostAddress = hostField.getText().trim();
                int port = Integer.parseInt(portField.getText().trim());
                String dbName = dbNameField.getText().trim();
                String username = dbUsernameField.getText().trim();
                String password = dbPasswordField.getText().trim();
                SessionFactory sessionFactory = dbConfig.useExternalDB(dbms, hostAddress, port, dbName, username, password);
                if (sessionFactory != null) {
                    DatabaseManagement.getInstance().setSessionFactory(sessionFactory);
                    connectionLabel.setText("پایگاه داده نرم افزار به پایگاه داده خارجی تغییر یافت.");
                    connectionLabel.setTextFill(Color.GREEN);
                } else {
                    connectionLabel.setText("خطا در برقراری ارتباط");
                    connectionLabel.setTextFill(Color.RED);
                }
            }
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


    private boolean connectionPaneFieldsAreOK() {
        connectionLabel.setTextFill(Color.RED);
        if (hostField.getText().trim().isEmpty()) {
            connectionLabel.setText("آدرس میزبان نمی تواند خالی باشد.");
            hostField.requestFocus();
            return false;
        }
        if (portField.getText().trim().isEmpty()) {
            connectionLabel.setText("پورت نمی تواند خالی باشد.");
            portField.requestFocus();
            return false;
        }
        if (dbNameField.getText().trim().isEmpty()) {
            connectionLabel.setText("نام پایگاه داده نمی تواند خالی باشد.");
            dbNameField.requestFocus();
            return false;
        }
        if (dbUsernameField.getText().trim().isEmpty()) {
            connectionLabel.setText("نام کاربری نمی تواند خالی باشد.");
            dbUsernameField.requestFocus();
            return false;
        }
        if (dbPasswordField.getText().trim().isEmpty()) {
            connectionLabel.setText("رمز عبور نمی تواند خالی باشد.");
            dbPasswordField.requestFocus();
            return false;
        }
        return true;
    }
}
