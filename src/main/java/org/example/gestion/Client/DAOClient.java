package org.example.gestion.Client;

import org.example.gestion.DB.DB;


import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DAOClient {

    public List<model> findAll() {
        List<model> list = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, telephone, email, prix, produit, date, quantite FROM client ORDER BY id DESC";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new model(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getInt("telephone"),
                        rs.getString("email"),
                        rs.getInt("prix"),
                        rs.getString("produit"),
                        rs.getDate("date"),
                        rs.getInt("quantite")

                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(model f) {
        String sql = "INSERT INTO client (nom, prenom, telephone, email, prix, produit, date, quantite) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, f.getNom());
            ps.setString(2, f.getPrenom());
            ps.setInt(3, f.getTelephone());
            ps.setString(4, f.getEmail());
            ps.setInt(5, f.getPrix());
            ps.setString(6, f.getProduit());
            ps.setDate(7, new java.sql.Date(f.getDate().getTime())); // util.Date -> sql.Date
            ps.setInt(8, f.getQuantite());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(model f) {
        String sql = "UPDATE client SET nom=?, prenom=?, telephone=?, email=?, prix=?, produit=?, `date`=?, quantite=? WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, f.getNom());
            ps.setString(2, f.getPrenom());
            ps.setInt(3, f.getTelephone());
            ps.setString(4, f.getEmail());
            ps.setInt(5, f.getPrix());
            ps.setString(6, f.getProduit());
            ps.setDate(7, new java.sql.Date(f.getDate().getTime()));
            ps.setInt(8, f.getQuantite());
            ps.setInt(9, f.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM client WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // le produit plus acheté
    public String getProduitLePlusAcheteDuMois() {
        String sql = "SELECT produit, SUM(quantite) AS total_qte " +
                "FROM client " +
                "WHERE MONTH(`date`) = MONTH(CURDATE()) " +
                "AND YEAR(`date`) = YEAR(CURDATE()) " +
                "GROUP BY produit " +
                "ORDER BY total_qte DESC " +
                "LIMIT 1";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String produit = rs.getString("produit");
                int total = rs.getInt("total_qte");
                return produit + " (" + total + ")";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "— Aucun produit ce mois —";
    }

    // le produit moins achete
    public String getProduitLeMoinsAcheteDuMois() {
        String sql = "SELECT produit, SUM(quantite) AS total_qte " +
                "FROM client " +
                "WHERE MONTH(`date`) = MONTH(CURDATE()) " +
                "AND YEAR(`date`) = YEAR(CURDATE()) " +
                "GROUP BY produit " +
                "ORDER BY total_qte ASC " +
                "LIMIT 1";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("produit") + " (" + rs.getInt("total_qte") + ")";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "—";
    }
    //areachart prix par produit
    public Map<String, Integer> getPrixParProduit() {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT produit, SUM(prix * quantite) AS total_prix " +
                "FROM client GROUP BY produit ORDER BY produit";
        try (Connection c = DB.getConnection();
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


}
