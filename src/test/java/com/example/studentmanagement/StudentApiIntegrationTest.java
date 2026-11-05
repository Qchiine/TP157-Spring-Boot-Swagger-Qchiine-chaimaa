package com.example.studentmanagement;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifie l'API de bout en bout : contexte Spring complet, base MySQL reelle et
 * appels HTTP simules par MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("API REST des etudiants")
class StudentApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void preparer() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /students/save cree un etudiant et renvoie 201")
    void creationEtudiant() throws Exception {
        String corps = "{\"nom\":\"LACHGAR\",\"prenom\":\"Mohamed\",\"dateNaissance\":\"1985-09-01\"}";

        mockMvc.perform(post("/students/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nom").value("LACHGAR"))
                .andExpect(jsonPath("$.prenom").value("Mohamed"));
    }

    @Test
    @DisplayName("GET /students/all renvoie la liste complete")
    void listeEtudiants() throws Exception {
        enregistrer("LACHGAR", "Mohamed", "1985-09-01");
        enregistrer("BENNANI", "Salma", "1985-04-12");

        mockMvc.perform(get("/students/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /students/count renvoie le total")
    void comptage() throws Exception {
        enregistrer("A", "A", "1990-01-01");
        enregistrer("B", "B", "1991-01-01");
        enregistrer("C", "C", "1992-01-01");

        mockMvc.perform(get("/students/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));
    }

    @Test
    @DisplayName("GET /students/byYear agrege par annee de naissance")
    void repartitionParAnnee() throws Exception {
        enregistrer("A", "A", "1985-09-01");
        enregistrer("B", "B", "1985-04-12");
        enregistrer("C", "C", "1990-11-23");

        mockMvc.perform(get("/students/byYear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("DELETE /students/delete/{id} renvoie 204 puis 404")
    void suppression() throws Exception {
        Student etudiant = enregistrer("A", "A", "1990-01-01");

        mockMvc.perform(delete("/students/delete/" + etudiant.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/students/delete/" + etudiant.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /students/{id} renvoie l'etudiant ou 404")
    void rechercheParIdentifiant() throws Exception {
        Student etudiant = enregistrer("LACHGAR", "Mohamed", "1985-09-01");

        mockMvc.perform(get("/students/" + etudiant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("LACHGAR"));

        mockMvc.perform(get("/students/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("un nom vide est refuse par la validation")
    void validationNomVide() throws Exception {
        String corps = "{\"nom\":\"\",\"prenom\":\"Mohamed\"}";

        mockMvc.perform(post("/students/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("un homonyme est refuse par le service")
    void homonymeRefuse() throws Exception {
        enregistrer("LACHGAR", "Mohamed", "1985-09-01");

        IllegalArgumentException erreur = assertThrows(IllegalArgumentException.class,
                () -> enregistrer("LACHGAR", "Mohamed", "1990-01-01"));
        assertTrue(erreur.getMessage().contains("existe deja"));
    }

    @Test
    @DisplayName("GET /students/nesApres filtre par annee")
    void nesApres() throws Exception {
        enregistrer("A", "A", "1985-09-01");
        enregistrer("B", "B", "1995-04-12");

        mockMvc.perform(get("/students/nesApres").param("annee", "1990"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("la documentation OpenAPI est exposee")
    void documentationExposee() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("API de gestion des etudiants"))
                .andExpect(jsonPath("$.paths").exists());
    }

    @Test
    @DisplayName("le service compte correctement apres plusieurs insertions")
    void comptageService() {
        enregistrer("A", "A", "1990-01-01");
        enregistrer("B", "B", "1991-01-01");

        assertEquals(2L, studentService.countStudents());
    }

    private Student enregistrer(String nom, String prenom, String naissance) {
        try {
            Student student = new Student(nom, prenom,
                    new SimpleDateFormat("yyyy-MM-dd").parse(naissance));
            return studentService.save(student);
        } catch (java.text.ParseException echec) {
            throw new IllegalStateException(echec);
        }
    }
}
