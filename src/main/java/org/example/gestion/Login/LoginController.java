package org.example.gestion.Login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final AuthService auth = new AuthService();

    @FXML
    private void onLogin(ActionEvent e) {
        try {
            String email = emailField.getText();
            String pass  = passwordField.getText();

            User u = auth.login(email, pass);
            if (u == null) {
                info("Identifiants invalides");
                return;
            }
            Session.set(u);
            goToMain(e);
        } catch (IllegalArgumentException ex) {
            info(ex.getMessage());
        }
    }

    @FXML
    private void goRegister(ActionEvent e) {
        try {
            Stage st = (Stage) ((Node) e.getSource()).getScene().getWindow();
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/org/example/gestion/views/Creerlogin.fxml"));
            st.setScene(new Scene(fxml.load(),  1260, 950));
            st.setTitle("Créer un compte");
        } catch (Exception ex) {
            info("Erreur navigation: " + ex.getMessage());
        }
    }

    private void goToMain(ActionEvent e) {
        try {
            Stage st = (Stage) ((Node) e.getSource()).getScene().getWindow();

            // 1) Chemin ABSOLU + casse exacte
            String path = "/org/example/gestion/views/Index.fxml";   // si le fichier est src/main/resources/views/Index.fxml
            URL url = getClass().getResource(path);
            Objects.requireNonNull(url, "Index.fxml introuvable (mauvais chemin/casse ou pas sous resources)");
            FXMLLoader fxml = new FXMLLoader(url);
            st.setScene(new Scene(fxml.load(), 1260, 950));
            st.setTitle("Accueil");
            st.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            info("Erreur chargement UI: " + ex.getMessage());
        }
    }


    private void info(String m) {
        new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait();
    }
}
