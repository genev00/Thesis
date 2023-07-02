package com.genev.a100nts.server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SiteImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;

    private String src;

    public SiteImage(Site site, String src) {
        this.site = site;
        this.src = src;
    }

}