package org.bigcompany;

import java.io.*;
import java.util.*;

public class EmployeeDataService {

    public List<Employee> readEmployeesFromCSV(String filePath) throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                Employee employee = parseEmployeeFromCSVLine(line);
                employees.add(employee);
            }
        }

        return employees;
    }

    public List<Employee> readEmployeesFromResource(String resourceFileName) throws IOException {
        List<Employee> employees = new ArrayList<>();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceFileName);
        if (inputStream == null) {
            throw new FileNotFoundException("Resource file not found: " + resourceFileName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                Employee employee = parseEmployeeFromCSVLine(line);
                employees.add(employee);
            }
        }

        return employees;
    }

    private Employee parseEmployeeFromCSVLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid CSV line format: " + line);
        }

        int id = Integer.parseInt(parts[0].trim());
        String firstName = parts[1].trim();
        String lastName = parts[2].trim();
        double salary = Double.parseDouble(parts[3].trim());
        // Accept empty, missing, or extra managerId
        Integer managerId = null;
        if (parts.length > 4 && !parts[4].trim().isEmpty()) {
            managerId = Integer.parseInt(parts[4].trim());
        }
        Employee emp = new Employee(id, firstName, lastName, salary, managerId);
        System.out.println("Parsed employee: " + emp + ", managerId=" + managerId);
        return emp;
    }

    public void buildHierarchy(List<Employee> employees) {
        Map<Integer, Employee> employeeMap = new HashMap<>();

        // Create lookup map
        for (Employee employee : employees) {
            employeeMap.put(employee.getId(), employee);
        }

        // Build relationships
        for (Employee employee : employees) {
            if (employee.getManagerId() != null) {
                Employee manager = employeeMap.get(employee.getManagerId());
                if (manager == null) {
                    throw new IllegalStateException("Manager with ID " + employee.getManagerId() +
                            " not found for employee " + employee.getFullName());
                }
                employee.setManager(manager);
                manager.addSubordinate(employee);
            }
        }
    }
}
