package com.genev.a100nts.client.utils.rest;

import static com.genev.a100nts.client.utils.ActivityHolder.getActivity;
import static com.genev.a100nts.client.utils.rest.RestService.REST_TEMPLATE;
import static com.genev.a100nts.client.utils.rest.RestService.SERVER_URL;
import static com.genev.a100nts.client.utils.rest.RestService.setAuthHeaderAndExecute;

import com.genev.a100nts.client.R;
import com.genev.a100nts.client.entities.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public final class UserService {

    static final String ENDPOINT = "/users";
    static final String ENDPOINT_PUBLIC = UserService.ENDPOINT + RestService.ENDPOINT_PUBLIC;

    UserService() {
    }

    Boolean checkIfEmailExists(String email) {
        return setAuthHeaderAndExecute(() -> {
            final String url = RestService.SERVER_URL + UserService.ENDPOINT_PUBLIC + "/emailExists/" + email;
            ResponseEntity<Boolean> response = REST_TEMPLATE.getForEntity(
                    url, Boolean.class
            );
            return response.getBody();
        });
    }

    Object login(User user) {
        return setAuthHeaderAndExecute(() -> {
            final String url = RestService.SERVER_URL + UserService.ENDPOINT_PUBLIC + "/login";
            HttpEntity<User> entityUser = new HttpEntity<>(user);
            try {
                ResponseEntity<User> response = REST_TEMPLATE.exchange(
                        url, HttpMethod.POST, entityUser, User.class
                );
                return response.getBody();
            } catch (Exception e) {
                final String message = RestService.getExceptionMessage(e);
                if (message != null && message.contains("Incorrect password")) {
                    return getActivity().getString(R.string.wrong_password);
                }
                throw e;
            }
        });
    }

    User register(User user) {
        return setAuthHeaderAndExecute(() -> {
            final String url = RestService.SERVER_URL + UserService.ENDPOINT_PUBLIC + "/register";
            HttpEntity<User> entityUser = new HttpEntity<>(user);
            ResponseEntity<User> response = REST_TEMPLATE.exchange(
                    url, HttpMethod.POST, entityUser, User.class
            );
            return response.getBody();
        });
    }

    Object confirmCode(String code) {
        return setAuthHeaderAndExecute(() -> requestWithCode(
                SERVER_URL + ENDPOINT + "/confirmCode/" + code, HttpEntity.EMPTY
        ));
    }

    Object sendCode(String email) {
        final String url = RestService.SERVER_URL + UserService.ENDPOINT_PUBLIC + "/sendCode/" + email;
        return setAuthHeaderAndExecute(() -> REST_TEMPLATE.exchange(
                url, HttpMethod.POST, HttpEntity.EMPTY, Void.class
        ));
    }

    User update(User user) {
        return setAuthHeaderAndExecute(() -> {
            HttpEntity<User> entityUser = new HttpEntity<>(user);
            ResponseEntity<User> response = REST_TEMPLATE.exchange(
                    SERVER_URL + ENDPOINT + "/update", HttpMethod.PUT, entityUser, User.class
            );
            return response.getBody();
        });
    }

    Object resetPass(User user, String code) {
        final String url = RestService.SERVER_URL + UserService.ENDPOINT_PUBLIC + "/resetPass/" + code;
        return setAuthHeaderAndExecute(() -> requestWithCode(
                url, new HttpEntity<>(user)
        ));
    }

    User[] getUsersForRanking() {
        return setAuthHeaderAndExecute(() -> {
            ResponseEntity<? extends User[]> response = REST_TEMPLATE.getForEntity(
                    SERVER_URL + ENDPOINT + "/forRanking", User[].class
            );
            return response.getBody();
        });
    }

    User addSiteToFavourites(Long siteId) {
        return setAuthHeaderAndExecute(() -> {
            ResponseEntity<User> response = REST_TEMPLATE.exchange(
                    SERVER_URL + ENDPOINT + "/favouriteSites/" + siteId, HttpMethod.PUT, HttpEntity.EMPTY, User.class
            );
            return response.getBody();
        });
    }

    User removeSiteFromFavourites(Long siteId) {
        return setAuthHeaderAndExecute(() -> {
            ResponseEntity<User> response = REST_TEMPLATE.exchange(
                    SERVER_URL + ENDPOINT + "/favouriteSites/" + siteId, HttpMethod.DELETE, HttpEntity.EMPTY, User.class
            );
            return response.getBody();
        });
    }

    User visitSites(Long[] ids) {
        return setAuthHeaderAndExecute(() -> {
            HttpEntity<Long[]> entityIds = new HttpEntity<>(ids);
            ResponseEntity<User> response = REST_TEMPLATE.exchange(
                    SERVER_URL + ENDPOINT + "/visit", HttpMethod.POST, entityIds, User.class
            );
            return response.getBody();
        });
    }

    private Object requestWithCode(String url, HttpEntity<?> entity) {
        try {
            ResponseEntity<User> response = REST_TEMPLATE.exchange(
                    url, HttpMethod.PUT, entity, User.class
            );
            return response.getBody();
        } catch (Exception e) {
            final String message = RestService.getExceptionMessage(e);
            if (message != null && message.contains("Incorrect security code")) {
                return getActivity().getString(R.string.wrong_security_code);
            }
            throw e;
        }
    }

}
