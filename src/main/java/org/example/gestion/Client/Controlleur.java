package org.example.gestion.Client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.function.Predicate;

public class Controlleur {

    // le produit plud achete
    @FXML private Label lblTopProduit;
    // le produit moins acheté
    @FXML private Label lblLowProduit;
    //areachart prix par produit
    @FXML private AreaChart<String, Number> chartPrixProduit;
    // Table + colonnes (donne ces fx:id à tes colonnes dans Scene Builder)
    @FXML private TableView<model> tableclient;
    @FXML private TableColumn<model, Integer> colId, colPrix, coltel, colQuantite;
    @FXML private TableColumn<model, String>  colNom, colPrenom, colEmail, colProduit;
    @FXML private TableColumn<model, Date>    colDate;

    // Champ de recherche (fx:id à mettre sur ton TextField de recherche)
    @FXML private TextField txtRecherche;

    private final DAOClient dao = new DAOClient();
    private final ObservableList<model> baseData = FXCollections.observableArrayList();
    private FilteredList<model> filtered;

    @FXML
    public void initialize() {
        // le produit plus achete
        String topProduit = dao.getProduitLePlusAcheteDuMois();
        lblTopProduit.setText(topProduit);
        // le produit moins acheté
        lblLowProduit.setText(dao.getProduitLeMoinsAcheteDuMois());
        // areachart prix par produit
        Map<String, Integer> prixProduits = dao.getPrixParProduit();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Prix total par produit");

        for (var entry : prixProduits.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chartPrixProduit.getData().clear();
        chartPrixProduit.getData().add(series);
        // mapping colonnes -> getters du modèle
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        coltel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colProduit.setCellValueFactory(new PropertyValueFactory<>("produit"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite")); // si renommé: "quantite"
        refreshTable();

        // recherche live
        filtered = new FilteredList<>(baseData, f -> true);
        if (txtRecherche != null) {
            txtRecherche.textProperty().addListener((obs, old, val) -> {
                String q = val == null ? "" : val.trim().toLowerCase();
                filtered.setPredicate(makeFilter(q));
            });
            SortedList<model> sorted = new SortedList<>(filtered);
            sorted.comparatorProperty().bind(tableclient.comparatorProperty());
            tableclient.setItems(sorted);
        } else {
            tableclient.setItems(baseData);
        }
    }

    private Predicate<model> makeFilter(String q) {
        if (q.isBlank()) return f -> true;
        return f -> String.valueOf(f.getId()).contains(q)
                || safe(f.getNom()).contains(q)
                || safe(f.getPrenom()).contains(q)
                || safe(f.getEmail()).contains(q)
                || safe(f.getProduit()).contains(q)
                || String.valueOf(f.getPrix()).contains(q)
                || String.valueOf(f.getQuantite()).contains(q)
                || (f.getDate() != null && f.getDate().toString().toLowerCase().contains(q));
    }

    private String safe(String s) { return s == null ? "" : s.toLowerCase(); }

    private void refreshTable() {
        baseData.setAll(dao.findAll());
        if (tableclient != null && (tableclient.getItems() == null || tableclient.getItems().isEmpty())) {
            tableclient.setItems(FXCollections.observableArrayList(baseData));
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
        model sel = tableclient.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Sélectionne un client."); return; }
        openEditForm(sel);
    }

    // Bouton "Supprimer" (onAction="#supprimer" sur ta page Liste)
    @FXML
    private void supprimer() {
        model sel = tableclient.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Sélectionne un client à supprimer."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le client ID " + sel.getId() + " ?", ButtonType.YES, ButtonType.NO);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/Ajouteclient.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un client");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
            error("Impossible d'ouvrir le formulaire d'ajout.");
        }
    }

    private void openEditForm(model client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/EditClient.fxml"));
            Parent root = loader.load();
            EditClientController ctrl = loader.getController();
            ctrl.setClient(client);
            Stage stage = new Stage();
            stage.setTitle("Modifier un client");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/RDV.fxml"));
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
