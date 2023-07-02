package com.genev.a100nts.server;

import com.genev.a100nts.server.repositories.SiteRepository;
import com.genev.a100nts.server.services.impl.DbSeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class Application {

    @Autowired
    private DbSeedService dbSeedService;
    @Autowired
    private SiteRepository siteRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener
    void seedDatabase(ContextRefreshedEvent event) {
        if (siteRepository.count() == 0) {
            dbSeedService.fillDb();
        }
    }

}
