package org.example.gestion.Login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;   // ✅ JavaFX PasswordField
    @FXML private PasswordField confirmField;    // ✅ idem

    private final AuthService auth = new AuthService();

    @FXML
    private void onRegister() {
        try {
            auth.register(
                    nameField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    confirmField.getText()
            );
            info("Compte créé." +
                    " Vous pouvez vous connecter.");
            goLogin();
        } catch (IllegalArgumentException ex) {
            info(ex.getMessage());
        }
    }

    @FXML
    private void goLogin() {
        try {
            Stage st = (Stage) nameField.getScene().getWindow();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/org/example/gestion/views/login.fxml"));
            st.setScene(new Scene(fxml.load(), 1260, 950));
            st.setTitle("Connexion");
        } catch (Exception ex) {
            info("Erreur navigation: " + ex.getMessage());
        }
    }

    private void info(String m) {
        new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait();
    }
}
