package org.example.gestion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.example.gestion.RDV.model; // <-- IMPORTANT: on aligne avec la TableView

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class Controller {

    // les label dans le nav
    @FXML private Label client;
    @FXML private Label fournisseur;
    @FXML private Label rdv;
    @FXML private Label rdvt;

    // Les vars graphiques
    @FXML private StackedAreaChart<String, Number> stackedChart;      // Celui de Client et Fournisseur (total prix)
    @FXML private LineChart<String, Number> lineChartFournisseur;     // Prix et Quantité par fournisseur
    @FXML private BarChart<String, Number> chartBarPrixProduit;       // Total prix par produit (client)

    // Tableau RDV
    @FXML private TableView<model> tableRDV;
    @FXML private TableColumn<model, Integer> colId;
    @FXML private TableColumn<model, String>  colNom, colPrenom, colmotif;
    @FXML private TableColumn<model, Date>    colDate;
    // filtre
    @FXML private TextField txtRecherche;

    private final DAO dao = new DAO();
    private final ObservableList<model> baseData = FXCollections.observableArrayList();
    private FilteredList<model> filtered;

    @FXML
    private void initialize() {
        // --- table rdv pour mappé---
        if (colId != null)      colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colNom != null)     colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        if (colPrenom != null)  colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        if (colDate != null)    colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        if (colmotif != null)   colmotif.setCellValueFactory(new PropertyValueFactory<>("motif")); // "motif" en minuscule

        refreshTable();
       // pour le filtre
        if (tableRDV != null) {
            filtered = new FilteredList<>(baseData, f -> true);
            if (txtRecherche != null) {
                txtRecherche.textProperty().addListener((obs, old, val) -> {
                    String q = (val == null) ? "" : val.trim().toLowerCase();
                    filtered.setPredicate(makeFilter(q));
                });
            }
            SortedList<model> sorted = new SortedList<>(filtered != null ? filtered : baseData);
            // Pas de cast exotique :
            sorted.comparatorProperty().bind(tableRDV.comparatorProperty());
            tableRDV.setItems(sorted);
        }

        // --- label total de fournisseur
        if (fournisseur != null) {
            String topProduit = safeStr(dao.totalfournisseur());
            fournisseur.setText(topProduit);
        }
        // --- label total de rdv
        if (rdvt != null) {
            String totalrdv = safeStr(dao.rdvjour());
            rdvt.setText(totalrdv);
        }

        if (rdv != null)    rdv.setText(safeStr(dao.totalrdv()));
        if (client != null) client.setText(safeStr(dao.totalclient()));

        // --- BARCHART : CA par produit (client) ---
        if (chartBarPrixProduit != null) {
            Map<String, ?> prixParProduit = dao.getTotalPrixParProduit(); // Map<String, Number|String>
            var serieCA = new XYChart.Series<String, Number>();
            serieCA.setName("Chiffre d'affaires par produit");
            for (var e : prixParProduit.entrySet()) {
                serieCA.getData().add(new XYChart.Data<>(e.getKey(), toNumber(e.getValue())));
            }
            chartBarPrixProduit.getData().setAll(serieCA);
        }

        // --- LINECHART : prix & quantité par fournisseur ---
        if (lineChartFournisseur != null) {
            List<Map<String, Object>> rows = dao.getPrixEtQuantiteParFournisseur();
            var seriePrix = new XYChart.Series<String, Number>();   seriePrix.setName("Prix total");
            var serieQuantite = new XYChart.Series<String, Number>(); serieQuantite.setName("Quantité totale");
            for (var row : rows) {
                String nom = safeStr(row.get("nom"));
                Number prix = toNumber(row.get("prix"));
                Number qte  = toNumber(row.get("quantite"));
                seriePrix.getData().add(new XYChart.Data<>(nom, prix));
                serieQuantite.getData().add(new XYChart.Data<>(nom, qte));
            }
            lineChartFournisseur.getData().setAll(seriePrix, serieQuantite);
        }

        // --- STACKED AREA : Clients vs Fournisseurs ---
        if (stackedChart != null) {
            Map<String, ?> clientData      = castMap(dao.getTotalPrixParProduitClient());
            Map<String, ?> fournisseurData = castMap(dao.getTotalPrixParProduitFournisseur());

            var categories = new TreeSet<String>();
            categories.addAll(clientData.keySet());
            categories.addAll(fournisseurData.keySet());

            var serieClient = new XYChart.Series<String, Number>(); serieClient.setName("Clients");
            var serieFourn  = new XYChart.Series<String, Number>();  serieFourn.setName("Fournisseurs");

            for (String cat : categories) {
                serieClient.getData().add(new XYChart.Data<>(cat, toNumber(clientData.get(cat))));
                serieFourn.getData().add(new XYChart.Data<>(cat, toNumber(fournisseurData.get(cat))));
            }
            stackedChart.getData().setAll(serieClient, serieFourn);
        }
    }

    // ---------- Helpers (METHODES AU NIVEAU DE CLASSE, PAS DANS initialize) ----------

    private Predicate<model> makeFilter(String q) {
        if (q == null || q.isBlank()) return f -> true;
        return f -> String.valueOf(f.getId()).contains(q)
                || safeStr(f.getNom()).contains(q)
                || safeStr(f.getPrenom()).contains(q)
                // Retire cette ligne si RDV.model n'a pas email :
                || safeStr(getEmailIfAny(f)).contains(q)
                || (f.getDate() != null && f.getDate().toString().toLowerCase().contains(q));
    }

    private String getEmailIfAny(model f) {
        try { // évite un plantage si la classe n'a pas getEmail()
            return String.valueOf(model.class.getMethod("getEmail").invoke(f));
        } catch (Exception ignore) {
            return "";
        }
    }

    private String safeStr(Object s) {
        return (s == null) ? "" : s.toString();
    }

    private void refreshTable() {
        baseData.setAll(dao.findAll());
        if (tableRDV != null && (tableRDV.getItems() == null || tableRDV.getItems().isEmpty())) {
            tableRDV.setItems(FXCollections.observableArrayList(baseData));
        }
    }

    /** Convertit n’importe quel objet en Number. */
    private Number toNumber(Object o) {
        if (o == null) return 0;
        if (o instanceof Number n) return n;
        String s = o.toString().trim();
        if (s.isEmpty() || s.equalsIgnoreCase("null")) return 0;
        try {
            s = s.replace(',', '.');
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            System.err.println("Valeur non numérique -> '" + s + "' ; forcée à 0");
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, ?> castMap(Object m) {
        return (m instanceof Map) ? (Map<String, ?>) m : Collections.emptyMap();
    }

    // ---------- Navigation ----------
    @FXML private void accueil()     { openModal("/org/example/gestion/views/index.fxml",      "LA PAGE D'ACCUEIL"); }
    @FXML private void CLIENT()      { openModal("/org/example/gestion/views/Client.fxml",     "LA LISTE DE CLIENT"); }
    @FXML private void Fournisseur() { openModal("/org/example/gestion/views/Fournisseur.fxml","LA LISTE DE FOURNISSEUR"); }
    @FXML private void RDV()         { openModal("/org/example/gestion/views/Rdv.fxml",        "LA LISTE DE RDV"); }

    private void openModal(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Impossible d'ouvrir : " + fxmlPath);
        }
    }
}
