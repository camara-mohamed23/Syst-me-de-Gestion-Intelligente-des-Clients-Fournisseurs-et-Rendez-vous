package org.example.gestion;

import org.example.gestion.DB.DB;
import org.example.gestion.RDV.model;
import org.example.gestion.fournisseur.ModelFournisseur;

import java.sql.*;
import java.util.*;

import static org.example.gestion.DB.DB.getConnection;

public class DAO {

    public List<model> findAll() {
        List<model> list = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, motif, date\n" +
                "FROM RDV\n" +
                "WHERE DATE(date) = CURDATE()\n" +
                "ORDER BY id DESC;\n";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new model(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("motif"),
                        rs.getDate("date")

                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    // barchar TotalPrixParProdui
    public Map<String, Integer> getTotalPrixParProduit() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT produit, SUM(prix * quantite) AS total_prix " +
                "FROM client GROUP BY produit ORDER BY total_prix DESC";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("produit"), rs.getInt("total_prix"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //PrixEtQuantiteParFournisseur

    public List<Map<String, Object>> getPrixEtQuantiteParFournisseur() {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT nom, SUM(prix) AS total_prix, SUM(quantite) AS total_quantite " +
                "FROM fournisseur GROUP BY nom ORDER BY nom";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("nom", rs.getString("nom"));
                row.put("prix", rs.getInt("total_prix"));
                row.put("quantite", rs.getInt("total_quantite"));
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



    // stackedareachar
    /** StackedAreaChart : total prix par produit côté CLIENT */
    public Map<String, Integer> getTotalPrixParProduitClient() {
        String sql = """
            SELECT produit, SUM(prix * quantite) AS total
            FROM client
            GROUP BY produit
            ORDER BY produit
        """;
        Map<String, Integer> out = new LinkedHashMap<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.put(rs.getString("produit"), rs.getInt("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    /** StackedAreaChart : total prix par "produit" côté FOURNISSEUR (ici on utilise nom comme catégorie) */
    public Map<String, Integer> getTotalPrixParProduitFournisseur() {
        String sql = """
            SELECT nom AS produit, SUM(prix * quantite) AS total
            FROM fournisseur
            GROUP BY nom
            ORDER BY nom
        """;
        Map<String, Integer> out = new LinkedHashMap<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.put(rs.getString("produit"), rs.getInt("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    // le total de client
    public String totalclient() {
        String sql = "SELECT  COUNT(id) AS total_client " +
                "FROM client ";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return  + rs.getInt("total_client") + "";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    // le total de rdv
    public String totalrdv() {
        String sql = "SELECT  COUNT(id) AS total_rdv " +
                "FROM RDV ";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return  + rs.getInt("total_rdv") + "";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    // le total de rdv
    public String totalfournisseur() {
        String sql = "SELECT  COUNT(id) AS total_fourni " +
                "FROM fournisseur ";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return  + rs.getInt("total_fourni") + "";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    // le total de rdv du jour
    public String rdvjour() {
        String sql = "SELECT COUNT(*) AS total FROM RDV WHERE DATE(date) = CURDATE()";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return String.valueOf(rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }



}
