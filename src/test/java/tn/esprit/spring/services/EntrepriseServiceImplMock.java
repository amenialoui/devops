package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.entities.Entreprise;
import tn.esprit.spring.repository.EmployeRepository;
import tn.esprit.spring.repository.EntrepriseRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class EntrepriseServiceImplMock {

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private EntrepriseServiceImpl entrepriseService;

    @Captor
    private ArgumentCaptor<Date> dateCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
        Date startDate = new Date(120, 0, 1); // 2020-01-01
        Date endDate = new Date(121, 0, 1);   // 2021-01-01
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
        Date startDate = new Date(120, 0, 1);
        Date endDate = new Date(121, 0, 1);
        Entreprise mockEntreprise = new Entreprise("TestCompany", "RS");

        when(entrepriseRepository.findById(entrepriseId)).thenReturn(Optional.of(mockEntreprise));
        when(employeRepository.countEmployesByEntrepriseAndDate(entrepriseId, startDate)).thenReturn(0);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                entrepriseService.CompanyGrowthRate(entrepriseId, startDate, endDate)
        );
    }

    @Test
    void testCompanyGrowthRateWithNonExistentCompany() {
        // Arrange
        Long entrepriseId = 1L;
        Date startDate = new Date(120, 0, 1);
        Date endDate = new Date(121, 0, 1);

        when(entrepriseRepository.findById(entrepriseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                entrepriseService.CompanyGrowthRate(entrepriseId, startDate, endDate)
        );
    }


    @Test
    void testGetCompaniesWithHighestAverageTenureWithEmptyResult() {
        // Arrange
        when(entrepriseRepository.findCompaniesWithHighestAverageTenure()).thenReturn(Collections.emptyList());

        // Act
        List<Entreprise> result = entrepriseService.getCompaniesWithHighestAverageTenure();

        // Assert
        assertTrue(result.isEmpty());
        verify(entrepriseRepository, times(1)).findCompaniesWithHighestAverageTenure();
    }


    @Test
    void testCompanyGrowthRateWithNegativeGrowth() {
        // Arrange
        Long entrepriseId = 1L;
        Date startDate = new Date(120, 0, 1); // 2020-01-01
        Date endDate = new Date(121, 0, 1);   // 2021-01-01
        Entreprise mockEntreprise = new Entreprise("TestCompany", "RS");

        when(entrepriseRepository.findById(entrepriseId)).thenReturn(Optional.of(mockEntreprise));
        when(employeRepository.countEmployesByEntrepriseAndDate(eq(entrepriseId), any(Date.class)))
                .thenReturn(100)  // startDate
                .thenReturn(80);  // endDate

        // Act
        double growthRate = entrepriseService.CompanyGrowthRate(entrepriseId, startDate, endDate);

        // Assert
        assertEquals(-20.0, growthRate, 0.01);
        verify(employeRepository, times(2)).countEmployesByEntrepriseAndDate(eq(entrepriseId), dateCaptor.capture());
        List<Date> capturedDates = dateCaptor.getAllValues();
        assertEquals(startDate, capturedDates.get(0));
        assertEquals(endDate, capturedDates.get(1));
    }

    @Test
    void testCompanyGrowthRateWithSameEmployeeCount() {
        // Arrange
        Long entrepriseId = 1L;
        Date startDate = new Date(120, 0, 1); // 2020-01-01
        Date endDate = new Date(121, 0, 1);   // 2021-01-01
        Entreprise mockEntreprise = new Entreprise("TestCompany", "RS");

        when(entrepriseRepository.findById(entrepriseId)).thenReturn(Optional.of(mockEntreprise));
        when(employeRepository.countEmployesByEntrepriseAndDate(eq(entrepriseId), any(Date.class)))
                .thenReturn(100)  // Both for startDate and endDate
                .thenReturn(100);

        // Act
        double growthRate = entrepriseService.CompanyGrowthRate(entrepriseId, startDate, endDate);

        // Assert
        assertEquals(0.0, growthRate, 0.01);
    }
}
