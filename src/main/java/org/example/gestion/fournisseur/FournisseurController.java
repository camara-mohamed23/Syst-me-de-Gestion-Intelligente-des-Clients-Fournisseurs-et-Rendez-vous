package org.example.gestion.fournisseur;

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

public class FournisseurController {
    // Totaux
    @FXML private Label total;
    @FXML private Label quantité;

    // Table + colonnes (donne ces fx:id à tes colonnes dans Scene Builder)
    @FXML private TableView<ModelFournisseur> tableFournisseurs;
    @FXML private TableColumn<ModelFournisseur, Integer> colId, colPrix, colQuantite;
    @FXML private TableColumn<ModelFournisseur, String>  colNom, colPrenom, colMotif, colEmail, colProduit;
    @FXML private TableColumn<ModelFournisseur, Date>    colDate;

    // Champ de recherche (fx:id à mettre sur ton TextField de recherche)
    @FXML private TextField txtRecherche;

    private final DAOFournisseur dao = new DAOFournisseur();
    private final ObservableList<ModelFournisseur> baseData = FXCollections.observableArrayList();
    private FilteredList<ModelFournisseur> filtered;

    @FXML
    public void initialize() {
        // mapping colonnes -> getters du modèle
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantité")); // si renommé: "quantite"
        colProduit.setCellValueFactory(new PropertyValueFactory<>("produit"));

        refreshTable();
        // --- LABELS (null-safe) ---
        if (total != null) {
            String topProduit = (dao.total());
            total.setText(topProduit);
        }
        if (quantité != null) {
            String totalrdv = (dao.quantité());
            quantité.setText(totalrdv);
        }

        // recherche live
        filtered = new FilteredList<>(baseData, f -> true);
        if (txtRecherche != null) {
            txtRecherche.textProperty().addListener((obs, old, val) -> {
                String q = val == null ? "" : val.trim().toLowerCase();
                filtered.setPredicate(makeFilter(q));
            });
            SortedList<ModelFournisseur> sorted = new SortedList<>(filtered);
            sorted.comparatorProperty().bind(tableFournisseurs.comparatorProperty());
            tableFournisseurs.setItems(sorted);
        } else {
            tableFournisseurs.setItems(baseData);
        }
    }

    private Predicate<ModelFournisseur> makeFilter(String q) {
        if (q.isBlank()) return f -> true;
        return f -> String.valueOf(f.getId()).contains(q)
                || safe(f.getNom()).contains(q)
                || safe(f.getPrenom()).contains(q)
                || safe(f.getMotif()).contains(q)
                || safe(f.getEmail()).contains(q)
                || safe(f.getProduit()).contains(q)
                || String.valueOf(f.getPrix()).contains(q)
                || String.valueOf(f.getQuantité()).contains(q)
                || (f.getDate() != null && f.getDate().toString().toLowerCase().contains(q));
    }

    private String safe(String s) { return s == null ? "" : s.toLowerCase(); }

    private void refreshTable() {
        baseData.setAll(dao.findAll());
        if (tableFournisseurs != null && (tableFournisseurs.getItems() == null || tableFournisseurs.getItems().isEmpty())) {
            tableFournisseurs.setItems(FXCollections.observableArrayList(baseData));
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
        ModelFournisseur sel = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Sélectionne un fournisseur."); return; }
        openEditForm(sel);
    }

    // Bouton "Supprimer" (onAction="#supprimer" sur ta page Liste)
    @FXML
    private void supprimer() {
        ModelFournisseur sel = tableFournisseurs.getSelectionModel().getSelectedItem();
        if (sel == null) { error("Sélectionne un fournisseur à supprimer."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer le fournisseur ID " + sel.getId() + " ?", ButtonType.YES, ButtonType.NO);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/AddFournisseur.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un fournisseur");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
            error("Impossible d'ouvrir le formulaire d'ajout.");
        }
    }

    private void openEditForm(ModelFournisseur fournisseur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestion/views/EditFournisseur.fxml"));
            Parent root = loader.load();
            EditFournisseurController ctrl = loader.getController();
            ctrl.setFournisseur(fournisseur);
            Stage stage = new Stage();
            stage.setTitle("Modifier un fournisseur");
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
