package org.example.gestion.Login;


import org.example.gestion.DB.DB;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class UserDAO {

    /** Inscription avec hash BCrypt. */
    public void register(String nom, String email, String rawPassword) {
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        String sql = "INSERT INTO login(nom, adresse, password) VALUES(?,?,?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** Retourne l'utilisateur si email+mot de passe sont valides, sinon null. */
    public User authenticate(String email, String rawPassword) {
        String sql = "SELECT id, nom, adresse, password FROM login WHERE adresse=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password");
                    if (BCrypt.checkpw(rawPassword, hash)) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("nom"),
                                rs.getString("adresse")
                        );
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM login WHERE adresse=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

}