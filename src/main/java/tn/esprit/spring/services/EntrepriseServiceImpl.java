package tn.esprit.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Departement;
import tn.esprit.spring.entities.Employe;
import tn.esprit.spring.entities.Entreprise;


import tn.esprit.spring.repository.EntrepriseRepository;
import tn.esprit.spring.repository.EmployeRepository;
import java.util.Date;
import java.util.List;

@Service
public class EntrepriseServiceImpl implements IEntrepriseService {
    @Autowired
    EntrepriseRepository entrepriseRepository;
    @Autowired
    EmployeRepository employeRepository;


    public double calculerMasseSalarialeEntreprise(Integer entrepriseId) {
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));

        double masseSalariale = 0.0;
        List<Departement> departements = entreprise.getDepartements();

        for (Departement departement : departements) {
            List<Employe> employes = departement.getEmployes();
            for (Employe employe : employes) {
                if (employe.getContrat() != null) {
                    masseSalariale += employe.getContrat().getSalaire();
                }
            }
        }

        return masseSalariale;
    }




    public List<Entreprise> getCompaniesWithHighestAverageTenure() {
        return entrepriseRepository.findCompaniesWithHighestAverageTenure();
    }

    public List<Entreprise> getCompaniesWithHighestAverageSalary() {
        return entrepriseRepository.findCompaniesWithHighestAverageSalary();
    }

    public double CompanyGrowthRate(Long entrepriseId, Date startDate, Date endDate) {
        // Récupérer l'entreprise
        Entreprise entreprise = (Entreprise) entrepriseRepository.findById(entrepriseId).orElse(null);
        if (entreprise == null) {
            throw new RuntimeException("Entreprise non trouvée");
        }

        // Récupérer le nombre d'employés au début et à la fin de la période
        int employesStart = employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, startDate);
        int employesEnd = employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, endDate);

        // Vérification pour éviter la division par zéro
        if (employesStart == 0) {
            throw new RuntimeException("Pas d'employés trouvés au début de la période");
        }

        // Calcul du taux de croissance
        double growthRate = ((double) (employesEnd - employesStart) / employesStart) * 100;

        return growthRate;
    }

}
