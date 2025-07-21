package com.waisl.keycloak.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@Data
public class Address {


    @Id
 //   @GeneratedValue
    private int empId ;
    private String city;
    private double code;

    public Address( int empId,String city, double code) {
        this.empId=empId;
        this.city = city;
        this.code = code;
    }
}
