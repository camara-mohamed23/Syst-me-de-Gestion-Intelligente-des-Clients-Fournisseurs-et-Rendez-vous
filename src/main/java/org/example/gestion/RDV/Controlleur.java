package org.example.gestion.RDV;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Date;
import java.util.function.Predicate;

public class Controlleur {


    // Table + colonnes (donne ces fx:id à tes colonnes dans Scene Builder)
    @FXML private TableView<model> tableRDV;
    @FXML private TableColumn<model, Integer> colId, coltel;
    @FXML private TableColumn<model, String>  colNom, colPrenom, colEmail,colMotif;
    @FXML private TableColumn<model, Date>    colDate;

    // Champ de recherche (fx:id à mettre sur ton TextField de recherche)
    @FXML private TextField txtRecherche;

    private final DAORdv dao = new DAORdv();
    private final ObservableList<model> baseData = FXCollections.observableArrayList();
    private FilteredList<model> filtered;

    @FXML
    public void initialize() {

        // mapping colonnes -> getters du modèle
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        coltel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("Motif")); // si renommé: "quantite"
        refreshTable();

        // recherche live
        filtered = new FilteredList<>(baseData, f -> true);
        if (txtRecherche != null) {
            txtRecherche.textProperty().addListener((obs, old, val) -> {
                String q = val == null ? "" : val.trim().toLowerCase();
                filtered.setPredicate(makeFilter(q));
            });
            SortedList<model> sorted = new SortedList<>(filtered);
            sorted.comparatorProperty().bind(tableRDV.comparatorProperty());
            tableRDV.setItems(sorted);
        } else {
            tableRDV.setItems(baseData);
        }
    }

    private Predicate<model> makeFilter(String q) {
        if (q.isBlank()) return f -> true;
        return f -> String.valueOf(f.getId()).contains(q)
                || safe(f.getNom()).contains(q)
                || safe(f.getPrenom()).contains(q)
                || safe(f.getEmail()).contains(q)
                || (f.getDate() != null && f.getDate().toString().toLowerCase().contains(q));
    }

    private String safe(String s) { return s == null ? "" : s.toLowerCase(); }

    private void refreshTable() {
        baseData.setAll(dao.findAll());
        if (tableRDV != null && (tableRDV.getItems() == null || tableRDV.getItems().isEmpty())) {
            tableRDV.setItems(FXCollections.observableArrayList(baseData));
        }
    }

    // Bouton "Ajouter" (onAction="#ajouter" sur ta page Liste)
    @FXML
    private void ajouter() {
        openAddForm();
    }

    // Bouton "Modifier" (onAction="#modifier" sur ta page Liste)
    @FXML
    private void modifier() {
        model sel = tableRDV.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Sélectionne un rendez-vous."); return; }
        openEditForm(sel);
    }

    // Bouton "Supprimer" (onAction="#supprimer" sur ta page Liste)
    @FXML
    private void supprimer() {
        model sel = tableRDV.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Sélectionne un rendez-vous à supprimer."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer rdv identifier" + sel.getId() + " ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                if (dao.delete(sel.getId())) refreshTable();
                else error("Suppression échouée.");
            }
        });
    }

    private void openAddForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/Ajouterdv.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un rdv");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
            error("Impossible d'ouvrir le formulaire d'ajout.");
        }
    }

    private void openEditForm(model rdv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/EditRDV.fxml"));
            Parent root = loader.load();
            EditRdvController ctrl = loader.getController();
            ctrl.setRdv(rdv);
            Stage stage = new Stage();
            stage.setTitle("Modifier un rendez-vous");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
            error("Impossible d'ouvrir le formulaire de modification.");
        }
    }

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
            refreshTable();
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
            refreshTable();
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
            refreshTable();
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
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
            error("Impossible d'ouvrir la page de RDV.");
        }
    }

    private void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
