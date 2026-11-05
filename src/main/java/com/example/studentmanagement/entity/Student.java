package com.example.studentmanagement.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.Objects;

/**
 * Etudiant enregistre dans la base.
 * <p>
 * Les contraintes de validation sont portees par l'entite : elles sont
 * verifiees automatiquement par Spring avant toute ecriture, et documentees
 * dans Swagger a partir des annotations Jakarta.
 */
@Entity
@Table(name = "student")
@Schema(name = "Student", description = "Un etudiant du cursus")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant genere par la base", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private int id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 40, message = "Le nom ne depasse pas 40 caracteres")
    @Column(nullable = false, length = 40)
    @Schema(description = "Nom de famille", example = "LACHGAR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 40, message = "Le prenom ne depasse pas 40 caracteres")
    @Column(nullable = false, length = 40)
    @Schema(description = "Prenom usuel", example = "Mohamed", requiredMode = Schema.RequiredMode.REQUIRED)
    private String prenom;

    @Past(message = "La date de naissance doit etre dans le passe")
    @Temporal(TemporalType.DATE)
    @Column(name = "date_naissance")
    @Schema(description = "Date de naissance au format aaaa-MM-jj", example = "1985-09-01")
    private Date dateNaissance;

    /** Constructeur requis par JPA. */
    public Student() {
    }

    /**
     * @param nom           nom de famille
     * @param prenom        prenom usuel
     * @param dateNaissance date de naissance
     */
    public Student(String nom, String prenom, Date dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    @Override
    public boolean equals(Object autre) {
        if (this == autre) {
            return true;
        }
        if (!(autre instanceof Student)) {
            return false;
        }
        Student etudiant = (Student) autre;
        return id != 0 && id == etudiant.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}
