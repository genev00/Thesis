package com.genev.a100nts.server.utils;

import com.genev.a100nts.server.dto.UserDTO;
import com.genev.a100nts.server.models.Site;
import com.genev.a100nts.server.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDTO userToDTOForRanking(User user) {
        return userToDTO(user, true, false, false);
    }

    public static UserDTO userToDTOForAdmin(User user) {
        return userToDTO(user, false, false, true);
    }

    public static UserDTO userToDTOForUser(User user) {
        return userToDTO(user, false, true, false);
    }

    public static List<UserDTO> usersToDTOsForRanking(List<User> users) {
        return users.stream()
                .map(UserMapper::userToDTOForRanking)
                .collect(Collectors.toList());
    }

    public static List<UserDTO> usersToDTOsForAdmin(List<User> users) {
        return users.stream()
                .map(UserMapper::userToDTOForAdmin)
                .collect(Collectors.toList());
    }

    private static UserDTO userToDTO(User user, boolean isForRanking, boolean isForUser, boolean isForAdmin) {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        if (isForRanking || isForUser) {
            userDTO.setVisitedSites(getVisitedSites(user));
        }
        if (isForUser || isForAdmin) {
            userDTO.setId(user.getId());
            userDTO.setRole(user.getRole());
            userDTO.setEmail(user.getEmail());
        }
        if (isForUser) {
            userDTO.setRanking(user.isRanking());
            userDTO.setConfirmCode(user.isConfirmCode());
            userDTO.setFavouriteSites(getFavouriteSites(user));
            userDTO.setVisitedSitesTime(user.getVisitedSitesTime());
        }
        return userDTO;
    }

    private static List<Long> getFavouriteSites(User user) {
        return user.getFavouriteSites() != null
                ? user.getFavouriteSites().stream()
                .map(Site::getId)
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static List<Long> getVisitedSites(User user) {
        return user.getVisitedSites() != null
                ? user.getVisitedSites().stream()
                .map(Site::getId)
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

}
