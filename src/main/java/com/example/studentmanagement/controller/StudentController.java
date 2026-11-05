package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

/**
 * Endpoints REST de gestion des etudiants.
 * <p>
 * Le controleur ne contient aucune logique metier : il recoit la requete,
 * appelle le service et construit la reponse HTTP correspondante. Chaque methode
 * est documentee par les annotations springdoc pour apparaitre dans Swagger UI.
 */
@RestController
@RequestMapping("/students")
@CrossOrigin
@Tag(name = "Etudiants", description = "Operations CRUD et statistiques sur les etudiants")
public class StudentController {

    private final StudentService studentService;

    /**
     * @param studentService service injecte par Spring
     */
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Enregistre un nouvel etudiant.
     *
     * @param student donnees de l'etudiant
     * @return l'etudiant cree avec le statut 201
     */
    @PostMapping("/save")
    @Operation(summary = "Enregistrer un etudiant",
            description = "Cree un etudiant ; un homonyme est refuse par le service")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Etudiant cree"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides ou homonyme")
    })
    public ResponseEntity<Student> save(@Valid @RequestBody Student student) {
        Student enregistre = studentService.save(student);
        return new ResponseEntity<>(enregistre, HttpStatus.CREATED);
    }

    /**
     * Met a jour un etudiant existant.
     *
     * @param id      identifiant de l'etudiant
     * @param student nouvelles valeurs
     * @return l'etudiant mis a jour, ou 404
     */
    @PutMapping("/update/{id}")
    @Operation(summary = "Mettre a jour un etudiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etudiant mis a jour"),
            @ApiResponse(responseCode = "404", description = "Identifiant inconnu")
    })
    public ResponseEntity<Student> update(
            @Parameter(description = "Identifiant de l'etudiant", example = "1")
            @PathVariable("id") int id,
            @Valid @RequestBody Student student) {
        return studentService.findById(id)
                .map(existant -> {
                    student.setId(id);
                    return new ResponseEntity<>(studentService.save(student), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Supprime un etudiant.
     *
     * @param id identifiant de l'etudiant
     * @return 204 si supprime, 404 sinon
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Supprimer un etudiant")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Etudiant supprime"),
            @ApiResponse(responseCode = "404", description = "Identifiant inconnu")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant de l'etudiant", example = "1")
            @PathVariable("id") int id) {
        return studentService.delete(id)
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Liste tous les etudiants.
     *
     * @return la liste complete
     */
    @GetMapping("/all")
    @Operation(summary = "Lister tous les etudiants")
    @ApiResponse(responseCode = "200", description = "Liste renvoyee")
    public ResponseEntity<List<Student>> findAll() {
        return new ResponseEntity<>(studentService.findAll(), HttpStatus.OK);
    }

    /**
     * Recherche un etudiant par identifiant.
     *
     * @param id identifiant recherche
     * @return l'etudiant, ou 404
     */
    @GetMapping("/{id}")
    @Operation(summary = "Rechercher un etudiant par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etudiant trouve"),
            @ApiResponse(responseCode = "404", description = "Identifiant inconnu")
    })
    public ResponseEntity<Student> findById(@PathVariable("id") int id) {
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Compte les etudiants enregistres.
     *
     * @return le nombre total
     */
    @GetMapping("/count")
    @Operation(summary = "Compter les etudiants")
    @ApiResponse(responseCode = "200", description = "Nombre renvoye")
    public ResponseEntity<Long> countStudent() {
        return new ResponseEntity<>(studentService.countStudents(), HttpStatus.OK);
    }

    /**
     * Repartit les etudiants par annee de naissance.
     *
     * @return des couples annee / effectif
     */
    @GetMapping("/byYear")
    @Operation(summary = "Repartir les etudiants par annee de naissance")
    @ApiResponse(responseCode = "200", description = "Repartition renvoyee")
    public ResponseEntity<Collection<Object[]>> findByYear() {
        return new ResponseEntity<>(studentService.findNbrStudentByYear(), HttpStatus.OK);
    }

    /**
     * Liste les etudiants nes apres une annee.
     *
     * @param annee annee de reference
     * @return les etudiants correspondants
     */
    @GetMapping("/nesApres")
    @Operation(summary = "Lister les etudiants nes apres une annee")
    @ApiResponse(responseCode = "200", description = "Liste renvoyee")
    public ResponseEntity<List<Student>> nesApres(
            @Parameter(description = "Annee de reference", example = "1990")
            @RequestParam("annee") int annee) {
        return new ResponseEntity<>(studentService.findNesApres(annee), HttpStatus.OK);
    }
}
