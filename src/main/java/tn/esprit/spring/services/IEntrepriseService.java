package tn.esprit.spring.services;

import tn.esprit.spring.entities.Entreprise;

import java.time.LocalDate;
import java.util.List;

public interface IEntrepriseService {
    List<Entreprise> getCompaniesWithHighestAverageTenure();
    double CompanyGrowthRate(Long entrepriseId, LocalDate startDate, LocalDate endDate);
    List<Entreprise> getCompaniesWithHighestAverageSalary();
}
