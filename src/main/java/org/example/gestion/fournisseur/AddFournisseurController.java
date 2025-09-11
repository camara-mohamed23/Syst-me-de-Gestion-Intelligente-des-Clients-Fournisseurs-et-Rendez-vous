package org.example.gestion.fournisseur;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static jdk.jpackage.internal.Log.error;


public class AddFournisseurController {

    @FXML private TextField txtNom, txtPrenom, txtMotif, txtEmail, txtPrix, txtQuantite, txtProduit;
    @FXML private DatePicker datePicker;

    private final DAOFournisseur dao = new DAOFournisseur();

    @FXML
    private void enregistrer() {
        if (!valid()) return;
        try {
            ModelFournisseur f = new ModelFournisseur(
                    null,
                    txtNom.getText().trim(),
                    txtPrenom.getText().trim(),
                    txtMotif.getText().trim(),
                    java.sql.Date.valueOf(datePicker.getValue()),
                    txtEmail.getText().trim(),
                    Integer.parseInt(txtPrix.getText().trim()),
                    Integer.parseInt(txtQuantite.getText().trim()),
                    txtProduit.getText().trim()
            );
            int id = dao.insert(f);
            if (id == -1) {
                alert(Alert.AlertType.ERROR, "Insertion échouée.");
                return;
            }
            retour(); // revenir à la liste
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.ERROR, "Prix et Quantité doivent être des entiers.");
        }
    }

    @FXML
    private void retour() {
        try {
            Parent list = FXMLLoader.load(getClass().getResource("/org/example/gestion/views/Fournisseur.fxml"));
            Stage stage = (Stage) txtNom.getScene().getWindow();
            stage.setTitle("Liste des fournisseurs");
            stage.setScene(new Scene(list));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Impossible de revenir à la liste : " + e.getMessage());
        }
    }

    private boolean valid() {
        if (isBlank(txtNom, txtPrenom, txtMotif, txtEmail, txtPrix, txtQuantite, txtProduit)
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
