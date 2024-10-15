package tn.esprit.spring.services;

import tn.esprit.spring.entities.Entreprise;

import java.util.Date;
import java.util.List;

public interface IEntrepriseService {
    List<Entreprise> getCompaniesWithHighestAverageTenure();
    double CompanyGrowthRate(Long entrepriseId, Date startDate, Date endDate);
    List<Entreprise> getCompaniesWithHighestAverageSalary();
}
