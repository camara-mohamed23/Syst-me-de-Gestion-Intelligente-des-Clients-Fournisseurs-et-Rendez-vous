package org.example.gestion.fournisseur;

import java.util.Date;

public class ModelFournisseur {
    private Integer id;
    private String nom;
    private String prenom;
    private String motif;
    private Date date;
    private String email;
    private int prix;
    private int quantité;
    private String produit;

    public ModelFournisseur(Integer id, String nom, String prenom, String motif, Date date, String email, int prix, int quantité, String produit) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.motif = motif;
        this.date = date;
        this.email = email;
        this.prix = prix;
        this.quantité = quantité;
        this.produit = produit;
    }

    public ModelFournisseur(int id, String nom, String prenom, String motif, java.sql.Date date) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.motif = motif;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public int getQuantité() {
        return quantité;
    }

    public void setQuantité(int quantité) {
        this.quantité = quantité;
    }

    public String getProduit() {
        return produit;
    }

    public void setProduit(String produit) {
        this.produit = produit;
    }
}
