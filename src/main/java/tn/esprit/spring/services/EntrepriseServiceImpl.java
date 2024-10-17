package tn.esprit.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Entreprise;


import tn.esprit.spring.repository.EntrepriseRepository;
import tn.esprit.spring.repository.EmployeRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class EntrepriseServiceImpl implements IEntrepriseService {

    private final EntrepriseRepository entrepriseRepository;
    private final EmployeRepository employeRepository;

    // Constructor injection
    @Autowired
    public EntrepriseServiceImpl(EntrepriseRepository entrepriseRepository, EmployeRepository employeRepository) {
        this.entrepriseRepository = entrepriseRepository;
        this.employeRepository = employeRepository;
    }



    public List<Entreprise> getCompaniesWithHighestAverageTenure() {
        return entrepriseRepository.findCompaniesWithHighestAverageTenure();
    }

    public List<Entreprise> getCompaniesWithHighestAverageSalary() {
        return entrepriseRepository.findCompaniesWithHighestAverageSalary();
    }

    public double CompanyGrowthRate(Long entrepriseId, LocalDate startDate, LocalDate endDate) {
        // Récupérer l'entreprise
        Entreprise entreprise = (Entreprise) entrepriseRepository.findById(entrepriseId).orElse(null);
        if (entreprise == null) {
            throw new IllegalArgumentException("Entreprise non trouvée");
        }

        // Récupérer le nombre d'employés au début et à la fin de la période
        int employesStart = employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, startDate);
        int employesEnd = employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, endDate);

        // Vérification pour éviter la division par zéro
        if (employesStart == 0) {
            throw new IllegalArgumentException("Pas d'employés trouvés au début de la période");
        }
        return ((double) (employesEnd - employesStart) / employesStart) * 100;
    }

}
