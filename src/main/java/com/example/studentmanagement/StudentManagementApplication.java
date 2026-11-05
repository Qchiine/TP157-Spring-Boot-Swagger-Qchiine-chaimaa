package com.example.studentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree de l'API de gestion des etudiants.
 * <p>
 * {@code @SpringBootApplication} active la configuration automatique, le scan
 * des composants du paquet racine et le serveur web embarque.
 */
@SpringBootApplication
public class StudentManagementApplication {

    /**
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }
}
