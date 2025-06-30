
import org.bigcompany.Employee;
import org.bigcompany.EmployeeAnalysisService;
import org.bigcompany.EmployeeDataService;
import org.bigcompany.EmployeeHierarchyAnalyzer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EmployeeHierarchyAnalyzerTest {

    private EmployeeDataService dataService;
    private EmployeeAnalysisService analysisService;

    @BeforeEach
    void setUp() {
        dataService = new EmployeeDataService();
        analysisService = new EmployeeAnalysisService();
    }

    @Test
    void testReadEmployeesFromCSV(@TempDir Path tempDir) throws IOException {
        // Create test CSV file
        Path csvFile = tempDir.resolve("test_employees.csv");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(csvFile))) {
            writer.println("Id,firstName,lastName,salary,managerId");
            writer.println("123,Joe,Doe,60000,");
            writer.println("124,Martin,Chekov,45000,123");
            writer.println("125,Bob,Ronstad,47000,123");
        }

        List<Employee> employees = dataService.readEmployeesFromCSV(csvFile.toString());

        assertEquals(3, employees.size());

        Employee ceo = employees.stream().filter(e -> e.getId() == 123).findFirst().orElse(null);
        assertNotNull(ceo);
        assertEquals("Joe", ceo.getFirstName());
        assertEquals("Doe", ceo.getLastName());
        assertEquals(60000.0, ceo.getSalary());
        assertNull(ceo.getManagerId());
    }

    @Test
    void testBuildHierarchy() {
        // Create test data
        List<Employee> employees = List.of(
                new Employee(123, "Joe", "Doe", 60000, null),
                new Employee(124, "Martin", "Chekov", 45000, 123),
                new Employee(125, "Bob", "Ronstad", 47000, 123)
        );

        dataService.buildHierarchy(employees);

        Employee ceo = employees.stream().filter(e -> e.getId() == 123).findFirst().orElse(null);
        Employee martin = employees.stream().filter(e -> e.getId() == 124).findFirst().orElse(null);
        Employee bob = employees.stream().filter(e -> e.getId() == 125).findFirst().orElse(null);

        assertNotNull(ceo);
        assertNotNull(martin);
        assertNotNull(bob);

        // Check CEO has 2 subordinates
        assertEquals(2, ceo.getSubordinates().size());
        assertTrue(ceo.getSubordinates().contains(martin));
        assertTrue(ceo.getSubordinates().contains(bob));

        // Check subordinates have correct manager
        assertEquals(ceo, martin.getManager());
        assertEquals(ceo, bob.getManager());
    }

    @Test
    void testEmployeeProperties() {
        Employee employee = new Employee(123, "John", "Smith", 50000, 456);

        assertEquals(123, employee.getId());
        assertEquals("John", employee.getFirstName());
        assertEquals("Smith", employee.getLastName());
        assertEquals(50000.0, employee.getSalary());
        assertEquals(456, employee.getManagerId());
        assertEquals("John Smith", employee.getFullName());
        assertFalse(employee.isCEO());
        assertFalse(employee.isManager());
    }

    @Test
    void testCEOIdentification() {
        Employee ceo = new Employee(123, "Boss", "Man", 100000, null);

        assertTrue(ceo.isCEO());
        assertNull(ceo.getManagerId());
    }

    @Test
    void testAnalyzeHierarchy(@TempDir Path tempDir) throws Exception {
        // Create test CSV file
        Path csvFile = tempDir.resolve("test_employees.csv");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(csvFile))) {
            writer.println("Id,firstName,lastName,salary,managerId");
            writer.println("123,Joe,Doe,60000,");
            writer.println("124,Martin,Chekov,45000,123");
            writer.println("125,Bob,Ronstad,47000,123");
        }

        // Test the complete flow
        EmployeeHierarchyAnalyzer analyzer = new EmployeeHierarchyAnalyzer();

        // This should not throw any exceptions
        assertDoesNotThrow(() -> analyzer.analyzeFromFile(csvFile.toString()));
    }
}