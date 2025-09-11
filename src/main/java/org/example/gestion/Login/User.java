package org.example.gestion.Login;

public class User {
    private int id;
    private String nom;
    private String email;
// On ne stocke pas le mot de passe en clair ici

    public User() {}

    public User(int id, String nom, String email) {
        this.id = id; this.nom = nom; this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override public String toString() { return nom + " " + " (" + email + ")"; }
}