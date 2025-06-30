package org.bigcompany;
import java.util.ArrayList;
import java.util.List;

public class Employee {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final double salary;
    private final Integer managerId;
    private final List<Employee> subordinates;
    private Employee manager;

    public Employee(int id, String firstName, String lastName, double salary, Integer managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
        this.subordinates = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public double getSalary() { return salary; }
    public Integer getManagerId() { return managerId; }
    public List<Employee> getSubordinates() { return subordinates; }
    public Employee getManager() { return manager; }

    public void setManager(Employee manager) { this.manager = manager; }
    public void addSubordinate(Employee subordinate) { this.subordinates.add(subordinate); }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isManager() {
        return !subordinates.isEmpty();
    }

 public boolean isCEO() {
     return managerId == null || managerId == 0;
 }

    @Override
    public String toString() {
        return String.format("%s (ID: %d, Salary: $%.2f)", getFullName(), id, salary);
    }
}
