package org.example.gestion.fournisseur;

import org.example.gestion.DB.DB;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOFournisseur {

    public List<ModelFournisseur> findAll() {
        List<ModelFournisseur> list = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, motif, date, email, prix, quantite, produit FROM fournisseur ORDER BY id DESC";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ModelFournisseur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("motif"),
                        rs.getDate("date"),
                        rs.getString("email"),
                        rs.getInt("prix"),
                        rs.getInt("quantite"),
                        rs.getString("produit")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(ModelFournisseur f) {
        String sql = "INSERT INTO fournisseur(nom, prenom, motif, date, email, prix, quantite, produit) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, f.getNom());
            ps.setString(2, f.getPrenom());
            ps.setString(3, f.getMotif());
            ps.setDate(4, new java.sql.Date(f.getDate().getTime()));
            ps.setString(5, f.getEmail());
            ps.setInt(6, f.getPrix());
            ps.setInt(7, f.getQuantité()); // si tu renommes: getQuantite()
            ps.setString(8, f.getProduit());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(ModelFournisseur f) {
        String sql = "UPDATE fournisseur SET nom=?, prenom=?, motif=?, date=?, email=?, prix=?, quantite=?, produit=? WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, f.getNom());
            ps.setString(2, f.getPrenom());
            ps.setString(3, f.getMotif());
            ps.setDate(4, new java.sql.Date(f.getDate().getTime()));
            ps.setString(5, f.getEmail());
            ps.setInt(6, f.getPrix());
            ps.setInt(7, f.getQuantité()); // si tu renommes: getQuantite()
            ps.setString(8, f.getProduit());
            ps.setInt(9, f.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM fournisseur WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // le total de rdv
    public String total() {
        String sql = "SELECT  SUM(prix) AS total " +
                "FROM fournisseur ";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return  + rs.getInt("total") + "";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "-";
    }

    // le total de rdv du jour
    public String quantité() {
        String sql = "SELECT MAX(quantite) AS quantité FROM fournisseur ";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return String.valueOf(rs.getInt("quantité"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "-";
    }
}
