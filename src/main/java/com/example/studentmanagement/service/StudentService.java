package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Logique metier des etudiants.
 * <p>
 * Le service encapsule l'acces au repository et porte les regles qui ne
 * relevent pas de la persistance, comme le refus d'un doublon nom + prenom.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    /**
     * @param studentRepository repository injecte par Spring
     */
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Enregistre un etudiant apres verification du doublon.
     *
     * @param student etudiant a enregistrer
     * @return l'etudiant persiste, identifiant compris
     * @throws IllegalArgumentException si un homonyme existe deja
     */
    @Transactional
    public Student save(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("L'etudiant ne peut pas etre nul");
        }
        List<Student> homonymes = studentRepository.findByNomIgnoreCase(student.getNom());
        boolean dejaPresent = homonymes.stream()
                .anyMatch(existant -> existant.getPrenom().equalsIgnoreCase(student.getPrenom())
                        && existant.getId() != student.getId());
        if (dejaPresent) {
            throw new IllegalArgumentException(
                    "Un etudiant " + student.getNom() + " " + student.getPrenom() + " existe deja");
        }
        return studentRepository.save(student);
    }

    /**
     * Supprime un etudiant par son identifiant.
     *
     * @param id identifiant de l'etudiant
     * @return vrai si la suppression a eu lieu
     */
    @Transactional
    public boolean delete(int id) {
        Optional<Student> trouve = studentRepository.findById(id);
        if (trouve.isEmpty()) {
            return false;
        }
        studentRepository.delete(trouve.get());
        return true;
    }

    /**
     * Liste tous les etudiants.
     *
     * @return la liste complete
     */
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    /**
     * Recherche un etudiant par identifiant.
     *
     * @param id identifiant recherche
     * @return l'etudiant, ou un Optional vide
     */
    @Transactional(readOnly = true)
    public Optional<Student> findById(int id) {
        return studentRepository.findById(id);
    }

    /**
     * @return le nombre total d'etudiants
     */
    @Transactional(readOnly = true)
    public long countStudents() {
        return studentRepository.count();
    }

    /**
     * Repartit les etudiants par annee de naissance.
     *
     * @return des couples annee / effectif
     */
    @Transactional(readOnly = true)
    public Collection<Object[]> findNbrStudentByYear() {
        return studentRepository.findNbrStudentByYear();
    }

    /**
     * Liste les etudiants nes apres une annee donnee.
     *
     * @param annee annee de reference
     * @return les etudiants correspondants
     */
    @Transactional(readOnly = true)
    public List<Student> findNesApres(int annee) {
        return studentRepository.findNesApres(annee);
    }
}
