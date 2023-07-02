package com.genev.a100nts.server.dto;

import com.genev.a100nts.server.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private UserRole role;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean ranking;
    private Boolean confirmCode;
    private List<Long> favouriteSites;
    private List<Long> visitedSites;
    private List<String> visitedSitesTime;

}
