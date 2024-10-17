package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Entreprise;
import tn.esprit.spring.repository.EmployeRepository;
import tn.esprit.spring.repository.EntrepriseRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntrepriseServiceImplMockTest {

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private EntrepriseServiceImpl entrepriseService;

    @Test
    void testGetCompaniesWithHighestAverageTenure() {
        // Arrange
        List<Entreprise> expectedCompanies = Arrays.asList(
                new Entreprise("Company1", "RS1"),
                new Entreprise("Company2", "RS2")
        );
        when(entrepriseRepository.findCompaniesWithHighestAverageTenure()).thenReturn(expectedCompanies);

        // Act
        List<Entreprise> result = entrepriseService.getCompaniesWithHighestAverageTenure();

        // Assert
        assertEquals(expectedCompanies, result);
        verify(entrepriseRepository).findCompaniesWithHighestAverageTenure();
    }

    @Test
    void testGetCompaniesWithHighestAverageSalary() {
        // Arrange
        List<Entreprise> expectedCompanies = Arrays.asList(
                new Entreprise("Company3", "RS3"),
                new Entreprise("Company4", "RS4")
        );
        when(entrepriseRepository.findCompaniesWithHighestAverageSalary()).thenReturn(expectedCompanies);

        // Act
        List<Entreprise> result = entrepriseService.getCompaniesWithHighestAverageSalary();

        // Assert
        assertEquals(expectedCompanies, result);
        verify(entrepriseRepository).findCompaniesWithHighestAverageSalary();
    }

    @Test
    void testCompanyGrowthRate() {
        // Arrange
        Long entrepriseId = 1L;
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 1);
        Entreprise mockEntreprise = new Entreprise("TestCompany", "RS");

        when(entrepriseRepository.findById(entrepriseId)).thenReturn(Optional.of(mockEntreprise));
        when(employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, startDate)).thenReturn(100);
        when(employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, endDate)).thenReturn(150);

        // Act
        double growthRate = entrepriseService.CompanyGrowthRate(entrepriseId, startDate, endDate);

        // Assert
        assertEquals(50.0, growthRate, 0.01);
        verify(entrepriseRepository).findById(entrepriseId);
        verify(employeRepository).countEmployesByEntrepriseAndDate(entrepriseId, startDate);
        verify(employeRepository).countEmployesByEntrepriseAndDate(entrepriseId, endDate);
    }

    @Test
    void testCompanyGrowthRateWithZeroInitialEmployees() {
        // Arrange
        Long entrepriseId = 1L;
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 1);
        Entreprise mockEntreprise = new Entreprise("TestCompany", "RS");

        when(entrepriseRepository.findById(entrepriseId)).thenReturn(Optional.of(mockEntreprise));
        when(employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, startDate)).thenReturn(0);
        when(employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, endDate)).thenReturn(10);

        // Act
        double growthRate = entrepriseService.CompanyGrowthRate(entrepriseId, startDate, endDate);

        // Assert
        assertEquals(100.0, growthRate, 0.01);
    }

    @Test
    void testCompanyGrowthRateWithNonExistentCompany() {
        // Arrange
        Long entrepriseId = 1L;
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 1);

        when(entrepriseRepository.findById(entrepriseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                entrepriseService.CompanyGrowthRate(entrepriseId, startDate, endDate)
        );
    }

    @Test
    void testCompanyGrowthRateWithNegativeGrowth() {
        // Arrange
        Long entrepriseId = 1L;
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 1);
        Entreprise mockEntreprise = new Entreprise("TestCompany", "RS");

        when(entrepriseRepository.findById(entrepriseId)).thenReturn(Optional.of(mockEntreprise));
        when(employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, startDate)).thenReturn(100);
        when(employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, endDate)).thenReturn(80);

        // Act
        double growthRate = entrepriseService.CompanyGrowthRate(entrepriseId, startDate, endDate);

        // Assert
        assertEquals(-20.0, growthRate, 0.01);
    }

    @Test
    void testCompanyGrowthRateWithSameEmployeeCount() {
        // Arrange
        Long entrepriseId = 1L;
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 1, 1);
        Entreprise mockEntreprise = new Entreprise("TestCompany", "RS");

        when(entrepriseRepository.findById(entrepriseId)).thenReturn(Optional.of(mockEntreprise));
        when(employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, startDate)).thenReturn(100);
        when(employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, endDate)).thenReturn(100);

        // Act
        double growthRate = entrepriseService.CompanyGrowthRate(entrepriseId, startDate, endDate);

        // Assert
        assertEquals(0.0, growthRate, 0.01);
    }
}