package com.genev.a100nts.server.controllers;

import com.genev.a100nts.server.dto.SiteDTO;
import com.genev.a100nts.server.dto.UserDTO;
import com.genev.a100nts.server.models.Site;
import com.genev.a100nts.server.models.UserRole;
import com.genev.a100nts.server.services.SiteService;
import com.genev.a100nts.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.genev.a100nts.server.utils.SiteMapper.*;
import static com.genev.a100nts.server.utils.UserMapper.userToDTOForAdmin;
import static com.genev.a100nts.server.utils.UserMapper.usersToDTOsForAdmin;

@RestController
@RequestMapping("/api/v1/admin")
@Validated
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private SiteService siteService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return new ResponseEntity<>(usersToDTOsForAdmin(userService.getAll()), HttpStatus.OK);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> changeUserRole(@NotNull @PathVariable Long id, @NotNull @RequestBody UserRole userRole) {
        return new ResponseEntity<>(userToDTOForAdmin(userService.updateRole(id, userRole)), HttpStatus.OK);
    }

    @GetMapping("/sites")
    public ResponseEntity<List<SiteDTO>> getSites() {
        return new ResponseEntity<>(sitesToDTOs(siteService.getAll()), HttpStatus.OK);
    }

    @GetMapping("/sites/bg")
    public ResponseEntity<List<SiteDTO>> getSitesBG() {
        return new ResponseEntity<>(sitesToDTOsBG(siteService.getAll()), HttpStatus.OK);
    }

    @GetMapping("/sites/{id}")
    public ResponseEntity<Site> getSiteDetails(@NotNull @PathVariable Long id) {
        return new ResponseEntity<>(simplifySite(siteService.getDetails(id)), HttpStatus.OK);
    }

    @PostMapping("/sites")
    public ResponseEntity<SiteDTO> addSite(@NotNull @Validated(Site.Add.class) @RequestBody Site site) {
        return new ResponseEntity<>(siteToDTO(siteService.add(site)), HttpStatus.OK);
    }

    @PostMapping("/sites/bg")
    public ResponseEntity<SiteDTO> addSiteBG(@NotNull @Validated(Site.Add.class) @RequestBody Site site) {
        return new ResponseEntity<>(siteToDTOBG(siteService.add(site)), HttpStatus.OK);
    }

    @PutMapping("/sites")
    public ResponseEntity<SiteDTO> updateSite(@NotNull @Validated(Site.Update.class) @RequestBody Site site) {
        return new ResponseEntity<>(siteToDTO(siteService.update(site)), HttpStatus.OK);
    }

    @PutMapping("/sites/bg")
    public ResponseEntity<SiteDTO> updateSiteBG(@NotNull @Validated(Site.Update.class) @RequestBody Site site) {
        return new ResponseEntity<>(siteToDTOBG(siteService.update(site)), HttpStatus.OK);
    }

}
