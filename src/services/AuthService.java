package services;

import models.Employee;
import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private Employee currentUser;
    private List<Employee> employeeList;

    public AuthService() {
        this.employeeList = FileService.loadEmployees();
    }

    // register a new employee into the system
    public boolean register(Employee employee) {
        if (employee == null || currentUser == null) return false;

        // Only manager can register
        if (!currentUser.getRole().equalsIgnoreCase("Manager")) {
            return false;
        }

        // Check duplicate ID
        for (Employee e : employeeList) {
            if (e.getId().equals(employee.getId())) {
                return false;
            }
        }

        employeeList.add(employee);

        // Save to CSV
        FileService.appendEmployee(employee);

        return true;
    }


    //log in using Employee object & pwd
    public boolean login(String id, String password) {
        for (Employee e : employeeList) {
            if (e.getId().equals(id) && e.getPassword().equals(password)) {
                currentUser = e;
                return true;
            }
        }
        return false;
    }

    // logout current user
    public void logout(){
        currentUser = null;
    }

    //check if someone is logged in
    public boolean isLoggedIn(){
        return currentUser != null;
    }

    //get current logged in user
    public Employee getCurrentUser(){
        return currentUser;
    }
}

