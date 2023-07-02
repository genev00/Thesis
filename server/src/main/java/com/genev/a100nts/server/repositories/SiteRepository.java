package com.genev.a100nts.server.repositories;

import com.genev.a100nts.server.models.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

}
