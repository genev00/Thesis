package com.genev.a100nts.server.controllers;

import com.genev.a100nts.server.dto.UserDTO;
import com.genev.a100nts.server.models.User;
import com.genev.a100nts.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.genev.a100nts.server.utils.UserMapper.userToDTOForUser;
import static com.genev.a100nts.server.utils.UserMapper.usersToDTOsForRanking;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/public/emailExists/{email}")
    public ResponseEntity<Boolean> doesEmailExist(@NotNull @Email @PathVariable String email) {
        return new ResponseEntity<>(userService.doesEmailExist(email), HttpStatus.OK);
    }

    @PostMapping("/public/register")
    public ResponseEntity<UserDTO> register(@NotNull @Validated(User.Register.class) @RequestBody User user) {
        return new ResponseEntity<>(userToDTOForUser(userService.register(user)), HttpStatus.OK);
    }

    @PutMapping("/confirmCode/{code}")
    public ResponseEntity<UserDTO> confirmCode(@NotNull @Pattern(regexp = "^[a-zA-Z0-9]{10}$") @PathVariable String code) {
        return new ResponseEntity<>(userToDTOForUser(userService.confirmCode(code)), HttpStatus.OK);
    }

    @PostMapping("/public/sendCode/{email}")
    public ResponseEntity<?> sendCode(@NotNull @Email @PathVariable String email) {
        userService.sendCode(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/public/login")
    public ResponseEntity<UserDTO> login(@NotNull @Validated(User.Login.class) @RequestBody User user) {
        return new ResponseEntity<>(userToDTOForUser(userService.login(user)), HttpStatus.OK);
    }

    @GetMapping("/forRanking")
    public ResponseEntity<List<UserDTO>> getForRanking() {
        return new ResponseEntity<>(usersToDTOsForRanking(userService.getAllForRanking()), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> update(@NotNull @Validated(User.Update.class) @RequestBody User user) {
        return new ResponseEntity<>(userToDTOForUser(userService.update(user)), HttpStatus.OK);
    }

    @PutMapping("/public/resetPass/{code}")
    public ResponseEntity<UserDTO> resetPass(@NotNull @Validated(User.ResetPass.class) @RequestBody User user, @NotNull @Pattern(regexp = "^[a-zA-Z0-9]{10}$") @PathVariable String code) {
        return new ResponseEntity<>(userToDTOForUser(userService.resetPass(user, code)), HttpStatus.OK);
    }

    @PutMapping("/favouriteSites/{siteId}")
    public ResponseEntity<UserDTO> addSiteToFavourites(@NotNull @PathVariable Long siteId) {
        return new ResponseEntity<>(userToDTOForUser(userService.addSiteToFavourites(siteId)), HttpStatus.OK);
    }

    @DeleteMapping("/favouriteSites/{siteId}")
    public ResponseEntity<UserDTO> removeSiteFromFavourites(@NotNull @PathVariable Long siteId) {
        return new ResponseEntity<>(userToDTOForUser(userService.removeSiteFromFavourites(siteId)), HttpStatus.OK);
    }

    @PostMapping("/visit")
    public ResponseEntity<UserDTO> visitSites(@NotNull @RequestBody Long[] siteIds) {
        return new ResponseEntity<>(userToDTOForUser(userService.visitSites(siteIds)), HttpStatus.OK);
    }

}
