package com.waisl.keycloak.service;

import com.waisl.keycloak.entity.Employee;
import com.waisl.keycloak.repository.EmployeeRepository;
import com.waisl.keycloak.utils.AesUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;


import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

//    @Autowired
//    RestTemplate restTemplate;

    @PostConstruct
    public void initializeEmployeeTable() {
        employeeRepository.saveAll(
                Stream.of(
                        new Employee("shanti", 20000),
                        new Employee("bhushan", 55000),
                        new Employee("ravi", 120000)
                ).collect(Collectors.toList()));
    }

    public Employee getEmployee(int employeeId) {
        return employeeRepository
                .findById(employeeId)
                .orElse(null);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository
                .findAll();
    }
    public  ResponseEntity<String>  getToken(String username,String password) throws Exception{

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/realms/waisl/protocol/openid-connect/token";
        AesUtil aesUtil= new AesUtil(128,1000);
        String decryptedPassword = aesUtil.decrypt(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("username", username);
        map.add("password", decryptedPassword);
        map.add("grant_type", "password");
        map.add("client_id", "springboot-keycloak-main");
        map.add("scope", "openid");
        //map.add("client_secret", "4MXAbsEwygUzj6ZhqmsIny9BAkWEYBg0");
//        map.add("public-client", "true");
//        map.add("bearer-only", "true");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        return restTemplate.postForEntity( url, request, String.class );
    }

    public  ResponseEntity<String>  getTokenDocker(String username,String password) throws Exception{

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://keycloak:8080/realms/waisl/protocol/openid-connect/token";
        AesUtil aesUtil= new AesUtil(128,1000);
        String decryptedPassword = aesUtil.decrypt(password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("username", username);
        map.add("password", decryptedPassword);
        map.add("grant_type", "password");
        map.add("client_id", "springboot-keycloak");
        map.add("scope", "openid");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        return restTemplate.postForEntity( url, request, String.class );
    }
}
