package com.example.studentmanagement;

import com.example.studentmanagement.controller.StudentController;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires du controleur, isoles de la base par Mockito.
 * <p>
 * Aucun contexte Spring n'est demarre : le service est simule, ce qui rend les
 * tests rapides et independants de MySQL.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Controleur des etudiants")
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private static Student etudiant(String nom, String prenom) {
        Student student = new Student();
        student.setNom(nom);
        student.setPrenom(prenom);
        return student;
    }

    @Test
    @DisplayName("la sauvegarde renvoie 201 avec l'etudiant cree")
    void testSaveStudent() {
        Student aEnregistrer = etudiant("Mido", "Karim");
        aEnregistrer.setId(1);
        when(studentService.save(any(Student.class))).thenReturn(aEnregistrer);

        ResponseEntity<Student> reponse = studentController.save(aEnregistrer);

        assertEquals(HttpStatus.CREATED, reponse.getStatusCode());
        assertNotNull(reponse.getBody());
        assertEquals("Mido", reponse.getBody().getNom());
        verify(studentService).save(aEnregistrer);
    }

    @Test
    @DisplayName("la suppression reussie renvoie 204")
    void testDeleteStudent() {
        when(studentService.delete(1)).thenReturn(true);

        ResponseEntity<Void> reponse = studentController.delete(1);

        assertEquals(HttpStatus.NO_CONTENT, reponse.getStatusCode());
        assertNull(reponse.getBody());
    }

    @Test
    @DisplayName("la suppression d'un identifiant inconnu renvoie 404")
    void testDeleteInconnu() {
        when(studentService.delete(99)).thenReturn(false);

        ResponseEntity<Void> reponse = studentController.delete(99);

        assertEquals(HttpStatus.NOT_FOUND, reponse.getStatusCode());
    }

    @Test
    @DisplayName("la liste complete est renvoyee avec 200")
    void testFindAllStudents() {
        List<Student> etudiants = Arrays.asList(etudiant("A", "A"), etudiant("B", "B"));
        when(studentService.findAll()).thenReturn(etudiants);

        ResponseEntity<List<Student>> reponse = studentController.findAll();

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertEquals(2, reponse.getBody().size());
    }

    @Test
    @DisplayName("une liste vide est renvoyee sans erreur")
    void testFindAllVide() {
        when(studentService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Student>> reponse = studentController.findAll();

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertTrue(reponse.getBody().isEmpty());
    }

    @Test
    @DisplayName("le comptage renvoie le total du service")
    void testCountStudents() {
        when(studentService.countStudents()).thenReturn(10L);

        ResponseEntity<Long> reponse = studentController.countStudent();

        assertEquals(10L, reponse.getBody());
        assertEquals(HttpStatus.OK, reponse.getStatusCode());
    }

    @Test
    @DisplayName("la repartition par annee est transmise telle quelle")
    void testFindByYear() {
        when(studentService.findNbrStudentByYear()).thenReturn(Arrays.asList());

        ResponseEntity<Collection<Object[]>> reponse = studentController.findByYear();

        assertEquals(0, reponse.getBody().size());
        assertEquals(HttpStatus.OK, reponse.getStatusCode());
    }

    @Test
    @DisplayName("la recherche par identifiant renvoie l'etudiant")
    void testFindById() {
        Student trouve = etudiant("LACHGAR", "Mohamed");
        trouve.setId(3);
        when(studentService.findById(3)).thenReturn(Optional.of(trouve));

        ResponseEntity<Student> reponse = studentController.findById(3);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertEquals("LACHGAR", reponse.getBody().getNom());
    }

    @Test
    @DisplayName("la recherche d'un identifiant inconnu renvoie 404")
    void testFindByIdInconnu() {
        when(studentService.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<Student> reponse = studentController.findById(99);

        assertEquals(HttpStatus.NOT_FOUND, reponse.getStatusCode());
    }

    @Test
    @DisplayName("la mise a jour d'un etudiant absent renvoie 404")
    void testUpdateInconnu() {
        when(studentService.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<Student> reponse = studentController.update(99, etudiant("X", "Y"));

        assertEquals(HttpStatus.NOT_FOUND, reponse.getStatusCode());
        verify(studentService, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("la mise a jour d'un etudiant present renvoie 200")
    void testUpdateExistant() {
        Student existant = etudiant("LACHGAR", "Mohamed");
        existant.setId(2);
        when(studentService.findById(2)).thenReturn(Optional.of(existant));
        when(studentService.save(any(Student.class))).thenReturn(existant);

        ResponseEntity<Student> reponse = studentController.update(2, etudiant("LACHGAR", "Ali"));

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
    }

    @Test
    @DisplayName("la recherche par annee transmet le parametre")
    void testNesApres() {
        when(studentService.findNesApres(1990)).thenReturn(List.of(etudiant("A", "A")));

        ResponseEntity<List<Student>> reponse = studentController.nesApres(1990);

        assertEquals(HttpStatus.OK, reponse.getStatusCode());
        assertEquals(1, reponse.getBody().size());
        verify(studentService).findNesApres(1990);
    }
}
