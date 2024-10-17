package tn.esprit.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.Entreprise;
import java.util.List;
import java.util.Optional;
@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Integer> {

    @Query("SELECT e FROM Entreprise e JOIN e.departements d JOIN d.employes emp JOIN emp.contrat c " +
            "GROUP BY e " +
            "ORDER BY AVG(DATEDIFF(CURRENT_DATE, c.dateDebut)) DESC")
    List<Entreprise> findCompaniesWithHighestAverageTenure();


    @Query("SELECT e FROM Entreprise e JOIN e.departements d JOIN d.employes emp JOIN emp.contrat c " +
            "GROUP BY e " +
            "ORDER BY AVG(c.salaire) DESC")
    List<Entreprise> findCompaniesWithHighestAverageSalary();


    Optional<Object> findById(Long entrepriseId);

}

