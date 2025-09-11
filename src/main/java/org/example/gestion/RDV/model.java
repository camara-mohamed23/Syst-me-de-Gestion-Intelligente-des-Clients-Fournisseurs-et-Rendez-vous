package org.example.gestion.RDV;

import java.util.Date;

public class model {
    private Integer id;
    private String nom;
    private  String prenom;
    private Integer telephone;
    private String motif;
    private Date date;
    private String email;

    public model(Integer id, String nom, String prenom, Integer telephone, String motif, Date date, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.motif = motif;
        this.date = date;
        this.email = email;
    }

    public model(Integer id, String nom, String prenom, String motif, Date date) {
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

    public Integer getTelephone() {
        return telephone;
    }

    public void setTelephone(Integer telephone) {
        this.telephone = telephone;
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
}
