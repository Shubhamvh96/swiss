package org.bigcompany;

// EmployeeAnalysisService.java
import java.util.*;

public class EmployeeAnalysisService {

    public void analyzeHierarchy(List<Employee> employees) {
        System.out.println("=== EMPLOYEE HIERARCHY ANALYSIS ===\n");

        analyzeSalaryViolations(employees);
        analyzeReportingLineLength(employees);
    }

    private void analyzeSalaryViolations(List<Employee> employees) {
        System.out.println("SALARY ANALYSIS:");
        System.out.println("================");

        List<String> underpaidManagers = new ArrayList<>();
        List<String> overpaidManagers = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.isManager()) {
                double avgSubordinateSalary = calculateAverageSubordinateSalary(employee);
                double minSalary = avgSubordinateSalary * 1.20; // 20% more
                double maxSalary = avgSubordinateSalary * 1.50; // 50% more

                if (employee.getSalary() < minSalary) {
                    double shortfall = minSalary - employee.getSalary();
                    underpaidManagers.add(String.format("%s earns $%.2f less than minimum (should earn at least $%.2f, currently earns $%.2f)",
                            employee.getFullName(), shortfall, minSalary, employee.getSalary()));
                } else if (employee.getSalary() > maxSalary) {
                    double excess = employee.getSalary() - maxSalary;
                    overpaidManagers.add(String.format("%s earns $%.2f more than maximum (should earn at most $%.2f, currently earns $%.2f)",
                            employee.getFullName(), excess, maxSalary, employee.getSalary()));
                }
            }
        }

        if (underpaidManagers.isEmpty()) {
            System.out.println("No managers earn less than they should.");
        } else {
            System.out.println("Managers earning less than they should:");
            for (String message : underpaidManagers) {
                System.out.println("  - " + message);
            }
        }

        System.out.println();

        if (overpaidManagers.isEmpty()) {
            System.out.println("No managers earn more than they should.");
        } else {
            System.out.println("Managers earning more than they should:");
            for (String message : overpaidManagers) {
                System.out.println("  - " + message);
            }
        }

        System.out.println();
    }

    private double calculateAverageSubordinateSalary(Employee manager) {
        List<Employee> subordinates = manager.getSubordinates();
        if (subordinates.isEmpty()) {
            return 0.0;
        }

        double totalSalary = subordinates.stream()
                .mapToDouble(Employee::getSalary)
                .sum();
        return totalSalary / subordinates.size();
    }

    private void analyzeReportingLineLength(List<Employee> employees) {
        System.out.println("REPORTING LINE ANALYSIS:");
        System.out.println("========================");

        Employee ceo = findCEO(employees);
        if (ceo == null) {
            System.out.println("Error: No CEO found in the organization.");
            throw new IllegalStateException("No CEO found in the organization.");
        } else {
            System.out.println("CEO: " + ceo.getFullName());
        }

        List<String> longReportingLines = new ArrayList<>();

        for (Employee employee : employees) {
            if (!employee.isCEO()) {
                int reportingLineLength = calculateReportingLineLength(employee, ceo);
                // Prevent infinite loop: if employee cannot reach CEO, break
                if (reportingLineLength > employees.size()) {
                    System.out.println("Warning: Employee " + employee.getFullName() + " does not reach CEO. Possible cycle or missing manager.");
                    continue;
                }
                if (reportingLineLength > 4) {
                    int excess = reportingLineLength - 4;
                    longReportingLines.add(String.format("%s has reporting line of %d levels (%d more than allowed)",
                            employee.getFullName(), reportingLineLength, excess));
                }
            }
        }

        if (longReportingLines.isEmpty()) {
            System.out.println("No employees have reporting lines that are too long.");
        } else {
            System.out.println("Employees with reporting lines longer than 4 levels:");
            for (String message : longReportingLines) {
                System.out.println("  - " + message);
            }
        }

        System.out.println();
    }

    private Employee findCEO(List<Employee> employees) {
        return employees.stream()
                .filter(Employee::isCEO)
                .findFirst()
                .orElse(null);
    }

    private int calculateReportingLineLength(Employee employee, Employee ceo) {
        int levels = 0;
        Employee current = employee;
        Set<Integer> visited = new HashSet<>(); // Prevent cycles
        while (current != null && !current.equals(ceo)) {
            if (visited.contains(current.getId())) {
                // Cycle detected
                return Integer.MAX_VALUE;
            }
            visited.add(current.getId());
            current = current.getManager();
            levels++;
        }
        // If CEO is not reached, return a large number
        if (current == null) return Integer.MAX_VALUE;
        return levels;
    }
}