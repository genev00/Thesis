package com.genev.a100nts.client.utils.rest;

import static com.genev.a100nts.client.utils.rest.RestService.REST_TEMPLATE;
import static com.genev.a100nts.client.utils.rest.RestService.SERVER_URL;
import static com.genev.a100nts.client.utils.rest.RestService.setAuthHeaderAndExecute;

import com.genev.a100nts.client.entities.Comment;
import com.genev.a100nts.client.entities.SiteUI;
import com.genev.a100nts.client.entities.Vote;
import com.genev.a100nts.client.utils.Language;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public final class SiteService {

    static final String ENDPOINT = "/sites";
    static final String ENDPOINT_PUBLIC = SiteService.ENDPOINT + RestService.ENDPOINT_PUBLIC;

    SiteService() {
    }

    SiteUI get(Long siteId) {
        return setAuthHeaderAndExecute(() -> {
            String url = RestService.SERVER_URL + SiteService.ENDPOINT_PUBLIC;
            if (!Language.isCurrentEnglish()) {
                url += RestService.ENDPOINT_BG;
            }
            url += "/" + siteId;
            ResponseEntity<SiteUI> response = REST_TEMPLATE.getForEntity(
                    url, SiteUI.class
            );
            return response.getBody();
        });
    }

    SiteUI[] getAll() {
        return setAuthHeaderAndExecute(() -> {
            String url = RestService.SERVER_URL + SiteService.ENDPOINT_PUBLIC;
            if (!Language.isCurrentEnglish()) {
                url += RestService.ENDPOINT_BG;
            }
            ResponseEntity<SiteUI[]> response = REST_TEMPLATE.getForEntity(
                    url, SiteUI[].class
            );
            return response.getBody();
        });
    }

    SiteUI[] getForVisiting() {
        return setAuthHeaderAndExecute(() -> {
            ResponseEntity<SiteUI[]> response = REST_TEMPLATE.getForEntity(
                    SERVER_URL + ENDPOINT + "/forVisiting", SiteUI[].class
            );
            return response.getBody();
        });
    }


    SiteUI vote(Long siteId, int voting) {
        return setAuthHeaderAndExecute(() -> {
            HttpEntity<Vote> voteEntity = new HttpEntity<>(new Vote(
                    siteId, voting, Language.getCurrent()
            ));
            ResponseEntity<SiteUI> response = REST_TEMPLATE.exchange(
                    SERVER_URL + ENDPOINT + "/vote", HttpMethod.POST, voteEntity, SiteUI.class
            );
            return response.getBody();
        });
    }

    Integer getVote(Long siteId) {
        return setAuthHeaderAndExecute(() -> {
            ResponseEntity<Integer> response = REST_TEMPLATE.getForEntity(
                    SERVER_URL + ENDPOINT + "/vote/" + siteId, Integer.class
            );
            return response.getBody();
        });
    }

    Comment postComment(Comment comment) {
        return setAuthHeaderAndExecute(() -> {
            HttpEntity<Comment> commentEntity = new HttpEntity<>(comment);
            ResponseEntity<Comment> response = REST_TEMPLATE.exchange(
                    SERVER_URL + ENDPOINT + "/comment", HttpMethod.POST, commentEntity, Comment.class
            );
            return response.getBody();
        });
    }

}
