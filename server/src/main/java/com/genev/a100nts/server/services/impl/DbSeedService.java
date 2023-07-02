package com.genev.a100nts.server.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genev.a100nts.server.models.Site;
import com.genev.a100nts.server.repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;

@Service
public class DbSeedService {

    private static final String SITES_DATA_FILE = "static/sites_data.json";
    private static Site[] parsedSites;

    static {
        loadSitesFromJson();
    }

    @Autowired
    private SiteRepository siteRepository;

    private static void loadSitesFromJson() {
        try {
            File file = new File(DbSeedService.class.getClassLoader().getResource(SITES_DATA_FILE).toURI());
            ObjectMapper objectMapper = new ObjectMapper();
            parsedSites = objectMapper.readValue(file, Site[].class);
            Arrays.stream(parsedSites).forEach(s -> {
                s.getImages().forEach(i -> i.setSite(s));
                s.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void fillDb() {
        if (parsedSites != null && parsedSites.length > 0) {
            siteRepository.saveAll(Arrays.asList(parsedSites));
        }
    }

}
