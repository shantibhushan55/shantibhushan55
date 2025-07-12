package com.waisl.keycloak;

import com.waisl.keycloak.entity.Employee;
import com.waisl.keycloak.entity.UserCredentials;
import com.waisl.keycloak.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/employees")
public class SpringBootKeycloakExampleApplication {

    @Autowired
    private EmployeeService service;

    //this method can be accessed by user whose role is user
    @GetMapping("/{employeeId}")
    @RolesAllowed("user")
    public ResponseEntity<Employee> getEmployee(@PathVariable int employeeId) {
        return ResponseEntity.ok(service.getEmployee(employeeId));
    }

    //this method can be accessed by user whose role is admin
    @GetMapping
    @RolesAllowed("admin")
    public ResponseEntity<List<Employee>> findALlEmployees() {
        return ResponseEntity.ok(service.getAllEmployees());
    }


    @GetMapping("/getToken")
    //@RolesAllowed("admin")
    public ResponseEntity<String> getToken(@RequestBody UserCredentials userCredentials) throws Exception {
        return service.getToken(userCredentials.getUsername(),userCredentials.getPassword());
    }

    @GetMapping("/docker/getToken")
    //@RolesAllowed("admin")
    public ResponseEntity<String> getTokenDocker(@RequestBody UserCredentials userCredentials) throws Exception {
        return service.getTokenDocker(userCredentials.getUsername(),userCredentials.getPassword());
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringBootKeycloakExampleApplication.class, args);
    }

}
