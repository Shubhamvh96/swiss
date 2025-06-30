package org.bigcompany;

// EmployeeHierarchyAnalyzer.java (Main Class)
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class EmployeeHierarchyAnalyzer {

    private final EmployeeDataService dataService;
    private final EmployeeAnalysisService analysisService;
    private static final String EMPLOYEES_CSV_FILE = "employees.csv";

    public EmployeeHierarchyAnalyzer() {
        this.dataService = new EmployeeDataService();
        this.analysisService = new EmployeeAnalysisService();
    }

    public static void main(String[] args) {
        EmployeeHierarchyAnalyzer analyzer = new EmployeeHierarchyAnalyzer();

        try {
            analyzer.processEmployeeDataFromResources();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processEmployeeDataFromResources() throws Exception {
        System.out.println("Reading employee data from resources: " + EMPLOYEES_CSV_FILE);

        // Read employees from CSV file in resources
        List<Employee> employees = dataService.readEmployeesFromResource(EMPLOYEES_CSV_FILE);
        System.out.println("Successfully loaded " + employees.size() + " employees\n");

        // Build hierarchy relationships
        dataService.buildHierarchy(employees);

        // Perform analysis
        analysisService.analyzeHierarchy(employees);
    }

    // Alternative method for testing or when file path is known
    public void analyzeFromFile(String filePath) throws Exception {
        System.out.println("Reading employee data from: " + filePath);

        // Read employees from CSV
        List<Employee> employees = dataService.readEmployeesFromCSV(filePath);
        System.out.println("Successfully loaded " + employees.size() + " employees\n");

        // Build hierarchy relationships
        dataService.buildHierarchy(employees);

        // Perform analysis
        analysisService.analyzeHierarchy(employees);
    }
}
