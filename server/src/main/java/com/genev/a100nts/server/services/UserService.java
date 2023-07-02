package com.genev.a100nts.server.services;

import com.genev.a100nts.server.models.User;
import com.genev.a100nts.server.models.UserRole;

import java.util.List;

public interface UserService {

    boolean doesEmailExist(String email);

    User register(User user);

    User confirmCode(String code);

    void sendCode(String email);

    User login(User user);

    List<User> getAll();

    List<User> getAllForRanking();

    User update(User user);

    User resetPass(User user, String code);

    User updateRole(Long id, UserRole role);

    User addSiteToFavourites(Long siteId);

    User removeSiteFromFavourites(Long siteId);

    User visitSites(Long[] siteIds);

}
