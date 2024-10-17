package tn.esprit.spring.services;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.entities.Employe;
import tn.esprit.spring.entities.Entreprise;
import tn.esprit.spring.repository.EmployeRepository;
import tn.esprit.spring.repository.EntrepriseRepository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class EntrepriseServiceImplTest {

    @Autowired
    private EntrepriseRepository entrepriseRepository;
    @Autowired
    private EntrepriseServiceImpl entrepriseService;
    @Autowired
    private EmployeRepository employeRepository;

    @Test
    void testFindCompaniesWithHighestAverageTenure() {
        List<Entreprise> entreprises = entrepriseRepository.findCompaniesWithHighestAverageTenure();

        assertNotNull(entreprises);
        assertFalse(entreprises.isEmpty(), "La liste d'entreprises ne doit pas être vide.");

        for (int i = 0; i < entreprises.size() - 1; i++) {
            double avgTenure1 = calculateAverageTenure(entreprises.get(i));
            double avgTenure2 = calculateAverageTenure(entreprises.get(i + 1));
            assertTrue(avgTenure1 >= avgTenure2,
                    "Les entreprises doivent être triées par ancienneté moyenne décroissante.");
        }

        Entreprise entrepriseAvecPlusHauteAncienneté = entreprises.get(0);
        System.out.println("Entreprise avec la plus haute ancienneté : " + entrepriseAvecPlusHauteAncienneté.getName());
    }

    @Test
    void testFindCompaniesWithHighestAverageSalary() {
        List<Entreprise> entreprises = entrepriseRepository.findCompaniesWithHighestAverageSalary();

        assertNotNull(entreprises);
        assertFalse(entreprises.isEmpty(), "La liste d'entreprises ne doit pas être vide.");

        for (int i = 0; i < entreprises.size() - 1; i++) {
            double avgSalary1 = calculateAverageSalary(entreprises.get(i));
            double avgSalary2 = calculateAverageSalary(entreprises.get(i + 1));
            assertTrue(avgSalary1 >= avgSalary2,
                    "Les entreprises doivent être triées par salaire moyen décroissant.");
        }

        Entreprise entrepriseAvecPlusHautSalaire = entreprises.get(0);
        System.out.println("Entreprise avec le salaire moyen le plus élevé : " + entrepriseAvecPlusHautSalaire.getName());
    }

    @Test
    void testCompanyGrowthRate()  {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse("2020-01-01", dateFormatter);  // Convertir la chaîne en LocalDate
        LocalDate endDate = LocalDate.now();

        List<Entreprise> entreprises = (List<Entreprise>) entrepriseRepository.findAll();
        assertFalse(entreprises.isEmpty(), "La base de données doit contenir au moins une entreprise pour ce test.");

        Entreprise entreprise = entreprises.get(0);
        double growthRate = entrepriseService.CompanyGrowthRate(entreprise.getId(), startDate, endDate);

        assertTrue(growthRate >= 0, "Le taux de croissance doit être supérieur ou égal à zéro.");
        System.out.println("Taux de croissance de l'entreprise : " + growthRate + "%");

        // Test avec un ID d'entreprise invalide
        Long invalidId = 999L;
        assertThrows(RuntimeException.class, () -> {
            entrepriseService.CompanyGrowthRate(invalidId, startDate, endDate);
        }, "Une exception devrait être levée pour un ID d'entreprise invalide");
    }

    private double calculateAverageTenure(Entreprise entreprise) {
        List<Employe> employes = employeRepository.findByDepartements_Entreprise(entreprise);
        if (employes.isEmpty()) {
            return 0.0;
        }

        long totalTenureInDays = 0;
        Date currentDate = new Date();
        for (Employe employe : employes) {
            if (employe.getContrat() != null && employe.getContrat().getDateDebut() != null) {
                long tenureInDays = (currentDate.getTime() - employe.getContrat().getDateDebut().getTime()) / (1000 * 60 * 60 * 24);
                totalTenureInDays += tenureInDays;
            }
        }

        return (double) totalTenureInDays / employes.size();
    }

    private double calculateAverageSalary(Entreprise entreprise) {
        List<Employe> employes = employeRepository.findByDepartements_Entreprise(entreprise);
        if (employes.isEmpty()) {
            return 0.0;
        }

        double totalSalary = 0;
        for (Employe employe : employes) {
            if (employe.getContrat() != null) {
                totalSalary += employe.getContrat().getSalaire();
            }
        }

        return totalSalary / employes.size();
    }



}
