package org.example.gestion.Login;




import java.util.regex.Pattern;

/** Règles métier: validations et appel au DAO. */
public class AuthService {
    private final UserDAO dao = new UserDAO();
    private static final Pattern EMAIL_RX = Pattern.compile("^[^@]+@[^@]+.[^@]+$");

    public void register(String nom, String email, String pass, String confirm) {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Nom requis");
        if (email == null || !EMAIL_RX.matcher(email).matches()) throw new IllegalArgumentException("Email invalide");
        if (pass == null || pass.length() < 6) throw new IllegalArgumentException("Mot de passe ≥ 6 caractères");
        if (!pass.equals(confirm)) throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        if (dao.emailExists(email)) throw new IllegalArgumentException("Email déjà utilisé");
        dao.register(nom , email, pass);
    }

    public User login(String email, String pass) {
        if (email == null || pass == null) throw new IllegalArgumentException("Champs requis");
        return dao.authenticate(email, pass);
    }
}