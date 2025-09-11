package org.example.gestion.RDV;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.time.LocalDate;

import static jdk.jpackage.internal.Log.error;

public class EditRdvController {

    @FXML private TextField txtNom, txtPrenom, txttel, txtEmail, txtMotif;
    @FXML private DatePicker datePicker;

    private final DAORdv dao = new DAORdv();
    private model courant;

    /** Appelée depuis la liste AVANT le switch de scène */
    public void setRdv(model f) {
        this.courant = f;
        remplirFormulaire();
    }

    private void remplirFormulaire() {
        if (courant == null) return;
        txtNom.setText(courant.getNom());
        txtPrenom.setText(courant.getPrenom());
        txtEmail.setText(courant.getEmail());
        txttel.setText(String.valueOf(courant.getTelephone()));
        txtMotif.setText(courant.getMotif());
        datePicker.setValue(courant.getDate()!=null
                ? new java.sql.Date(courant.getDate().getTime()).toLocalDate()
                : LocalDate.now());
    }

    @FXML
    private void mettreAJour() {
        if (!valid()) return;
        try {
            courant.setNom(txtNom.getText().trim());
            courant.setPrenom(txtPrenom.getText().trim());
            courant.setEmail(txtEmail.getText().trim());
            courant.setTelephone(Integer.parseInt(txttel.getText().trim()));
            courant.setDate(java.sql.Date.valueOf(datePicker.getValue()));

            if (!dao.update(courant)) {
                alert(Alert.AlertType.ERROR, "Mise à jour échouée.");
                return;
            }
            retour();
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.ERROR, "Prix et Quantité doivent être des entiers.");
        }
    }

    @FXML
    private void retour() {
        try {
            Parent list = FXMLLoader.load(getClass().getResource("/org/example/gestion/views/RDV.fxml"));
            Stage stage = (Stage) txtNom.getScene().getWindow();
            stage.setTitle("GESTION DE RDV");
            stage.setScene(new Scene(list));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Impossible de revenir à la liste : " + e.getMessage());
        }
    }

    private boolean valid() {
        if (courant == null) { alert(Alert.AlertType.ERROR, "Donnée manquante."); return false; }
        if (isBlank(txtNom, txtPrenom, txttel, txtEmail, txtMotif)
                || datePicker.getValue()==null) {
            alert(Alert.AlertType.ERROR, "Tous les champs sont obligatoires.");
            return false;
        }
        return true;
    }
    private boolean isBlank(TextField... fields) {
        for (TextField t : fields) if (t.getText()==null || t.getText().isBlank()) return true;
        return false;
    }
    private void alert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK); a.setHeaderText(null); a.showAndWait();
    }

    // les liens
    @FXML
    private void accueil() {
        accueil1();
    }
    @FXML
    private void CLIENT() {
        CLIENT1();
    }
    @FXML
    private void Fournisseur() {
        Fournisseur1();
    }
    @FXML
    private void RDV() {
        RDV1();
    }


    private void accueil1() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/index.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("LA PAGE D'ACCUEIL");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            error("Impossible d'ouvrir  la page index.");
        }
    }

    private void CLIENT1() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/Client.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("LA LISTE DE CLIENT");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            error("Impossible d'ouvrir la liste de client.");
        }
    }

    private void Fournisseur1() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/Fournisseur.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("la liste de fournisseur");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            error("Impossible d'ouvrir la page de fournisseur.");
        }
    }

    private void RDV1() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/Rdv.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("la liste de RDV");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            error("Impossible d'ouvrir la page de RDV.");
        }
    }
}
