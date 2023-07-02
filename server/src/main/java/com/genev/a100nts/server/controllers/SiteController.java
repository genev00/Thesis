package com.genev.a100nts.server.controllers;

import com.genev.a100nts.server.dto.CommentDTO;
import com.genev.a100nts.server.dto.SiteDTO;
import com.genev.a100nts.server.models.Site;
import com.genev.a100nts.server.services.SiteService;
import com.genev.a100nts.server.web.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.genev.a100nts.server.utils.SiteMapper.*;

@RestController
@RequestMapping("/api/v1/sites")
@Validated
public class SiteController {

    @Autowired
    private SiteService siteService;

    @GetMapping("/public")
    public ResponseEntity<List<SiteDTO>> getAll() {
        return new ResponseEntity<>(sitesToDTOs(siteService.getAllVisible()), HttpStatus.OK);
    }

    @GetMapping("/public/bg")
    public ResponseEntity<List<SiteDTO>> getAllBG() {
        return new ResponseEntity<>(sitesToDTOsBG(siteService.getAllVisible()), HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<SiteDTO> getDetails(@NotNull @PathVariable Long id) {
        return new ResponseEntity<>(siteToDTO(siteService.getDetails(id)), HttpStatus.OK);
    }

    @GetMapping("/public/bg/{id}")
    public ResponseEntity<SiteDTO> getDetailsBG(@NotNull @PathVariable Long id) {
        return new ResponseEntity<>(siteToDTOBG(siteService.getDetails(id)), HttpStatus.OK);
    }

    @GetMapping("/forVisiting")
    public ResponseEntity<List<SiteDTO>> getForVisiting() {
        return new ResponseEntity<>(sitesToDTOsForVisiting(siteService.getVisibleForVisiting()), HttpStatus.OK);
    }

    @PostMapping("/vote")
    public ResponseEntity<SiteDTO> vote(@NotNull @Valid @RequestBody Vote vote) {
        Site site = siteService.vote(vote);
        return new ResponseEntity<>(vote.getLanguage().equals("bg") ? siteToDTOBG(site) : siteToDTO(site), HttpStatus.OK);
    }

    @GetMapping("/vote/{siteId}")
    public ResponseEntity<Integer> getVote(@NotNull @PathVariable Long siteId) {
        return new ResponseEntity<>(siteService.getUserVote(siteId), HttpStatus.OK);
    }

    @PostMapping("/comment")
    public ResponseEntity<CommentDTO> comment(@NotNull @Valid @RequestBody CommentDTO comment) {
        return new ResponseEntity<>(commentToDTO(siteService.addComment(comment)), HttpStatus.OK);
    }

}
