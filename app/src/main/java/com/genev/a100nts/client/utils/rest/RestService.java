package com.genev.a100nts.client.utils.rest;

import static com.genev.a100nts.client.utils.ActivityHolder.getActivity;

import android.content.Intent;
import android.os.StrictMode;

import com.genev.a100nts.client.common.Constants;
import com.genev.a100nts.client.data.login.LoginRepository;
import com.genev.a100nts.client.entities.Comment;
import com.genev.a100nts.client.entities.Site;
import com.genev.a100nts.client.entities.SiteUI;
import com.genev.a100nts.client.entities.User;
import com.genev.a100nts.client.entities.UserRole;
import com.genev.a100nts.client.ui.error.ErrorActivity;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Supplier;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RestService {

    static final RestTemplate REST_TEMPLATE;
    static final String SERVER_URL;
    static final String ENDPOINT_PUBLIC;
    static final String ENDPOINT_BG;
    private static final UserService USER_SERVICE;
    private static final SiteService SITE_SERVICE;
    private static final AdminService ADMIN_SERVICE;

    static {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().permitAll().build()
        );

        REST_TEMPLATE = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        REST_TEMPLATE.setMessageConverters(converters);

        SERVER_URL = "http://IPv4:8080/api/v1";
        ENDPOINT_PUBLIC = "/public";
        ENDPOINT_BG = "/bg";

        USER_SERVICE = new UserService();
        SITE_SERVICE = new SiteService();
        ADMIN_SERVICE = new AdminService();
    }

    private RestService() {
    }

    static <T> T setAuthHeaderAndExecute(Supplier<T> supplier) {
        try {
            resetAuthorizationHeader();
            return supplier.get();
        } catch (Exception e) {
            final String message = getExceptionMessage(e);
            log.error(message, e);
            showError(message);
        }
        return null;
    }

    private static void resetAuthorizationHeader() {
        final List<ClientHttpRequestInterceptor> interceptors = REST_TEMPLATE.getInterceptors();
        if (LoginRepository.getInstance().isUserLogged()) {
            final User loggedUser = LoginRepository.getInstance().getLoggedUser();
            final byte[] credentialBytes = (loggedUser.getEmail() + ":" + loggedUser.getPassword()).getBytes();
            final String headerValue = "Basic " + Base64.getEncoder().encodeToString(credentialBytes);
            interceptors.add((request, body, execution) -> {
                request.getHeaders().set(HttpHeaders.AUTHORIZATION, headerValue);
                return execution.execute(request, body);
            });
        } else {
            interceptors.clear();
        }
    }

    static String getExceptionMessage(Exception e) {
        String message = e.getMessage();
        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException clientErrorException = (HttpClientErrorException) e;
            message = clientErrorException.getStatusCode().toString() + ": " + clientErrorException.getResponseBodyAsString();
        }
        return message;
    }

    public static Boolean checkIfEmailExists(String email) {
        return USER_SERVICE.checkIfEmailExists(email);
    }

    public static User registerUser(User user) {
        return USER_SERVICE.register(user);
    }

    public static Object confirmUserCode(String code) {
        return USER_SERVICE.confirmCode(code);
    }

    public static Object sendUserCode(String email) {
        return USER_SERVICE.sendCode(email);
    }

    public static Object loginUser(User user) {
        return USER_SERVICE.login(user);
    }

    public static User updateUser(User user) {
        return USER_SERVICE.update(user);
    }

    public static Object resetUserPass(User user, String code) {
        return USER_SERVICE.resetPass(user, code);
    }

    public static User[] getUsersForRanking() {
        return USER_SERVICE.getUsersForRanking();
    }

    public static User addSiteToFavourites(Long siteId) {
        return USER_SERVICE.addSiteToFavourites(siteId);
    }

    public static User removeSiteFromFavourites(Long siteId) {
        return USER_SERVICE.removeSiteFromFavourites(siteId);
    }

    public static User visitSites(Long[] ids) {
        return USER_SERVICE.visitSites(ids);
    }

    public static SiteUI getSite(Long siteId) {
        return SITE_SERVICE.get(siteId);
    }

    public static SiteUI[] getSites() {
        return SITE_SERVICE.getAll();
    }

    public static SiteUI[] getSitesForVisiting() {
        return SITE_SERVICE.getForVisiting();
    }

    public static SiteUI voteSite(Long siteId, int voting) {
        return SITE_SERVICE.vote(siteId, voting);
    }

    public static Integer getVote(Long siteId) {
        return SITE_SERVICE.getVote(siteId);
    }

    public static Comment postComment(Comment comment) {
        return SITE_SERVICE.postComment(comment);
    }

    public static User[] getUsersAsAdmin() {
        return ADMIN_SERVICE.getUsers();
    }

    public static User changeUserRole(Long userId, UserRole newRole) {
        return ADMIN_SERVICE.changeUserRole(userId, newRole);
    }

    public static SiteUI[] getSitesAsAdmin() {
        return ADMIN_SERVICE.getSites();
    }

    public static Site getSiteAsAdmin(Long siteId) {
        return ADMIN_SERVICE.getSite(siteId);
    }

    public static SiteUI addSite(Site site) {
        return ADMIN_SERVICE.addSite(site);
    }

    public static SiteUI updateSite(Site site) {
        return ADMIN_SERVICE.updateSite(site);
    }

    @SneakyThrows
    private static void showError(String message) {
        Intent errorIntent = new Intent(getActivity(), ErrorActivity.class);
        errorIntent.putExtra(Constants.ARGUMENT_ERROR_MESSAGE, message);
        getActivity().startActivity(errorIntent);
        getActivity().finish();
    }

}
