package com.genev.a100nts.client.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private UserRole role;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean ranking;
    private Boolean confirmCode;
    private List<Long> favouriteSites;
    private List<Long> visitedSites;
    private List<String> visitedSitesTime;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String password, Boolean ranking) {
        this(email, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.ranking = ranking;
    }

}
