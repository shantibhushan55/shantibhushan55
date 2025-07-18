package com.waisl.keycloak;

import com.waisl.keycloak.entity.Employee;
import com.waisl.keycloak.entity.UserCredentials;
import com.waisl.keycloak.service.EmployeeService;
import com.waisl.keycloak.utils.AesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/employees")
public class SpringBootKeycloakApplication {

    @Autowired
    private EmployeeService service;

    /**
     * this method can be accessed by user whose role is user
     */
    @GetMapping("/{employeeId}")
    //@RolesAllowed("user")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Employee> getEmployee(@PathVariable int employeeId) {
        return ResponseEntity.ok(service.getEmployee(employeeId));
    }


    /**
     * this method can be accessed by user whose role is admin
     */
    @GetMapping("/all")
    //@RolesAllowed("admin")
    @PreAuthorize("hasAnyRole('ROLE_SPRINGBOOT_ADMIN')")
    public ResponseEntity<List<Employee>> findALlEmployees() {

        return ResponseEntity.ok(service.getAllEmployees());
    }

    /**
     * any user can access this method to get access token
     */
    @GetMapping("/getToken")
    public ResponseEntity<String> getToken(@RequestBody UserCredentials userCredentials) throws Exception {
           return service.getToken(userCredentials.getUsername(), userCredentials.getPassword());
    }

    /**
     * @param userCredentials
     * @return
     * @throws Exception
     */
    @GetMapping("/docker/getToken")
    public ResponseEntity<String> getTokenDocker(@RequestBody UserCredentials userCredentials) throws Exception {
        return service.getTokenDocker(userCredentials.getUsername(), userCredentials.getPassword());
    }

    @GetMapping("/message")
    public String getMessage() throws Exception {
        return "Message from kubernetes deployment";
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringBootKeycloakApplication.class, args);
    }

}
