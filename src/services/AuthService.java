package services;

import models.Employee;
import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private Employee currentUser;
    private List<Employee> employeeList;

    public AuthService() {
        employeeList = new ArrayList<>();
    }

    // register a new employee into the system
    public boolean register(Employee employee) {
        if (employee == null) return false;

        // check duplicate ID
        for (Employee e : employeeList) {
            if (e.getId().equals(employee.getId())) {
                return false; // ID already exists
            }
        }

        employeeList.add(employee);
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

