package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Acces aux donnees des etudiants.
 * <p>
 * Spring Data JPA fournit l'implementation : les methodes derivees du nom sont
 * generees automatiquement, seules les requetes JPQL explicites sont ecrites.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    /**
     * Recherche un etudiant par son identifiant.
     *
     * @param id identifiant recherche
     * @return l'etudiant, ou un Optional vide
     */
    @Override
    Optional<Student> findById(Integer id);

    /**
     * Recherche les etudiants portant un nom donne.
     *
     * @param nom nom recherche, insensible a la casse
     * @return les etudiants correspondants
     */
    List<Student> findByNomIgnoreCase(String nom);

    /**
     * Compte les etudiants par annee de naissance.
     *
     * @return des couples annee / effectif
     */
    @Query("select year(s.dateNaissance), count(s) from Student s "
            + "where s.dateNaissance is not null group by year(s.dateNaissance) "
            + "order by year(s.dateNaissance)")
    Collection<Object[]> findNbrStudentByYear();

    /**
     * Recherche les etudiants nes apres une annee donnee.
     *
     * @param annee annee de reference
     * @return les etudiants plus jeunes
     */
    @Query("select s from Student s where year(s.dateNaissance) > :annee order by s.nom")
    List<Student> findNesApres(@Param("annee") int annee);
}
