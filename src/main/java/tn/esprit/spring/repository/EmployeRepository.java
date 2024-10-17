package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.Employe;
import tn.esprit.spring.entities.Entreprise;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Integer> {
    @Query("SELECT COUNT(e) FROM Employe e " +
            "JOIN e.departements d " +
            "JOIN d.entreprise ent " +
            "WHERE ent.id = :entrepriseId " +
            "AND e.contrat.dateDebut <= :date " +
            "AND (e.contrat.dateDebut IS NULL OR e.contrat.dateDebut <= :date)")
    int countEmployesByEntrepriseAndDate(@Param("entrepriseId") Long entrepriseId, @Param("date") LocalDate date);

    List<Employe> findByDepartements_Entreprise(Entreprise entreprise);
}
