package com.example.studentmanagement.config;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Jeu de donnees de demonstration.
 * <p>
 * Le remplissage n'a lieu que si la table est vide : relancer l'application ne
 * duplique donc jamais les etudiants.
 */
@Configuration
public class JeuDeDonneesConfig {

    /**
     * Insere huit etudiants au premier demarrage.
     *
     * @param repository repository des etudiants
     * @return le lanceur execute au demarrage
     */
    @Bean
    public CommandLineRunner remplirLaBase(StudentRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            List<Student> etudiants = new ArrayList<>();
            String[][] donnees = {
                    {"LACHGAR", "Mohamed", "1985-09-01"},
                    {"BENNANI", "Salma", "1985-04-12"},
                    {"ELIDRISSI", "Youssef", "1990-11-23"},
                    {"RAMI", "Hind", "1992-02-08"},
                    {"TAZI", "Omar", "1993-07-30"},
                    {"CHRAIBI", "Nadia", "1995-05-19"},
                    {"FASSI", "Karim", "1998-01-14"},
                    {"ALAOUI", "Wafa", "2000-10-05"},
            };
            for (String[] ligne : donnees) {
                try {
                    etudiants.add(new Student(ligne[0], ligne[1], format.parse(ligne[2])));
                } catch (ParseException echec) {
                    throw new IllegalStateException("Date invalide : " + ligne[2], echec);
                }
            }
            repository.saveAll(etudiants);
        };
    }
}
