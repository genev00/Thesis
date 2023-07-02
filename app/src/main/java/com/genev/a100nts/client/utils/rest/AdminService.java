package com.genev.a100nts.client.utils.rest;

import static com.genev.a100nts.client.utils.rest.RestService.REST_TEMPLATE;
import static com.genev.a100nts.client.utils.rest.RestService.SERVER_URL;
import static com.genev.a100nts.client.utils.rest.RestService.setAuthHeaderAndExecute;

import com.genev.a100nts.client.entities.Site;
import com.genev.a100nts.client.entities.SiteUI;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.entities.UserRole;
import com.genev.a100nts.client.utils.Language;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public final class AdminService {

    private static final String ENDPOINT = "/admin";

    AdminService() {
    }

    User[] getUsers() {
        return setAuthHeaderAndExecute(() -> {
            ResponseEntity<User[]> response = REST_TEMPLATE.getForEntity(
                    SERVER_URL + AdminService.ENDPOINT + UserService.ENDPOINT, User[].class
            );
            return response.getBody();
        });
    }

    User changeUserRole(Long userId, UserRole newRole) {
        return setAuthHeaderAndExecute(() -> {
            final String url = SERVER_URL + AdminService.ENDPOINT + UserService.ENDPOINT + "/" + userId;
            HttpEntity<UserRole> roleEntity = new HttpEntity<>(newRole);
            ResponseEntity<User> response = REST_TEMPLATE.exchange(
                    url, HttpMethod.PUT, roleEntity, User.class
            );
            return response.getBody();
        });
    }

    SiteUI[] getSites() {
        return setAuthHeaderAndExecute(() -> {
            String url = SERVER_URL + AdminService.ENDPOINT + SiteService.ENDPOINT;
            if (!Language.isCurrentEnglish()) {
                url += RestService.ENDPOINT_BG;
            }
            ResponseEntity<SiteUI[]> response = REST_TEMPLATE.getForEntity(
                    url, SiteUI[].class
            );
            return response.getBody();
        });
    }

    Site getSite(Long siteId) {
        return setAuthHeaderAndExecute(() -> {
            ResponseEntity<Site> response = REST_TEMPLATE.getForEntity(
                    SERVER_URL + AdminService.ENDPOINT + SiteService.ENDPOINT + "/" + siteId, Site.class
            );
            return response.getBody();
        });
    }

    SiteUI addSite(Site site) {
        return setAuthHeaderAndExecute(() -> {
            String url = SERVER_URL + AdminService.ENDPOINT + SiteService.ENDPOINT;
            if (!Language.isCurrentEnglish()) {
                url += RestService.ENDPOINT_BG;
            }
            HttpEntity<Site> siteEntity = new HttpEntity<>(site);
            ResponseEntity<SiteUI> response = REST_TEMPLATE.exchange(
                    url, HttpMethod.POST, siteEntity, SiteUI.class
            );
            return response.getBody();
        });
    }

    SiteUI updateSite(Site site) {
        return setAuthHeaderAndExecute(() -> {
            String url = SERVER_URL + AdminService.ENDPOINT + SiteService.ENDPOINT;
            if (!Language.isCurrentEnglish()) {
                url += RestService.ENDPOINT_BG;
            }
            HttpEntity<Site> siteEntity = new HttpEntity<>(site);
            ResponseEntity<SiteUI> response = REST_TEMPLATE.exchange(
                    url, HttpMethod.PUT, siteEntity, SiteUI.class
            );
            return response.getBody();
        });
    }

}
