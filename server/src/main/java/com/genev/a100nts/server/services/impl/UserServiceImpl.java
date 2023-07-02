package com.genev.a100nts.server.services.impl;

import com.genev.a100nts.server.exceptions.ClientException;
import com.genev.a100nts.server.models.Site;
import com.genev.a100nts.server.models.User;
import com.genev.a100nts.server.models.UserRole;
import com.genev.a100nts.server.repositories.SiteRepository;
import com.genev.a100nts.server.repositories.UserRepository;
import com.genev.a100nts.server.services.EmailService;
import com.genev.a100nts.server.services.UserService;
import com.genev.a100nts.server.utils.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.genev.a100nts.server.security.SecurityConfiguration.getCurrentUserEmail;

@Service
public class UserServiceImpl implements UserService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    private final static String ID_AND_DATETIME_SEPARATOR = "+";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Override
    public boolean doesEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public User register(User user) {
        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        generateAndSendSecurityCode(user);
        return userRepository.save(user);
    }

    @Override
    public User confirmCode(String code) {
        User user = userRepository.findByEmail(getCurrentUserEmail()).get();
        if (!user.getSecurityCode().equals(code)) {
            throw new ClientException("Incorrect security code");
        }
        user.setConfirmCode(false);
        return userRepository.save(user);
    }

    @Override
    public void sendCode(String email) {
        User user = userRepository.findByEmail(email).get();
        generateAndSendSecurityCode(user);
        userRepository.save(user);
    }

    @Override
    public User login(User user) {
        User userDb = userRepository.findByEmail(user.getEmail()).get();
        if (!passwordEncoder.matches(user.getPassword(), userDb.getPassword())) {
            throw new ClientException("Incorrect password");
        }
        if (userDb.getRole() == UserRole.ADMIN || userDb.isConfirmCode()) {
            generateAndSendSecurityCode(userDb);
        }
        return userRepository.save(userDb);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllForRanking() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.USER && u.isRanking() && !u.getVisitedSites().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public User update(User user) {
        User userDb = userRepository.findByEmail(getCurrentUserEmail()).get();
        userDb.setFirstName(user.getFirstName());
        userDb.setLastName(user.getLastName());
        if (!userDb.getEmail().equals(user.getEmail())) {
            userDb.setEmail(user.getEmail());
            generateAndSendSecurityCode(userDb);
        }
        if (user.getPassword() != null) {
            userDb.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userDb.setRanking(user.isRanking());
        return userRepository.save(userDb);
    }

    @Override
    public User resetPass(User user, String code) {
        User userDb = userRepository.findByEmail(user.getEmail()).get();
        if (!userDb.getSecurityCode().equals(code)) {
            throw new ClientException("Incorrect security code");
        }
        userDb.setPassword(passwordEncoder.encode(user.getPassword()));
        userDb.setConfirmCode(false);
        return userRepository.save(userDb);
    }

    @Override
    public User updateRole(Long id, UserRole role) {
        User user = userRepository.findById(id).get();
        if (user.getEmail().equals(getCurrentUserEmail())) {
            throw new ClientException(String.format("Administrator with ID %d cannot change their own role", id));
        }
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User addSiteToFavourites(Long siteId) {
        User user = userRepository.findByEmail(getCurrentUserEmail()).get();
        List<Site> favouriteSites = new ArrayList<>(user.getFavouriteSites());
        if (favouriteSites.stream().noneMatch(s -> s.getId().equals(siteId))) {
            favouriteSites.add(siteRepository.findById(siteId).get());
            user.setFavouriteSites(favouriteSites);
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User removeSiteFromFavourites(Long siteId) {
        User user = userRepository.findByEmail(getCurrentUserEmail()).get();
        List<Site> favouriteSites = new ArrayList<>(user.getFavouriteSites());
        if (favouriteSites.removeIf(s -> s.getId().equals(siteId))) {
            user.setFavouriteSites(favouriteSites);
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User visitSites(Long[] siteIds) {
        User user = userRepository.findByEmail(getCurrentUserEmail()).get();
        List<Site> visitedSites = new ArrayList<>(user.getVisitedSites());
        List<Site> newlyVisitedSites = Arrays.stream(siteIds)
                .filter(id -> !visitedSites.contains(id))
                .map(id -> siteRepository.findById(id).get())
                .collect(Collectors.toList());
        visitedSites.addAll(newlyVisitedSites);
        user.setVisitedSites(visitedSites);
        List<String> sitesTime = new ArrayList<>(user.getVisitedSitesTime());
        for (Site site : newlyVisitedSites) {
            sitesTime.add(site.getId() + ID_AND_DATETIME_SEPARATOR + LocalDateTime.now().format(DATE_TIME_FORMATTER));
        }
        user.setVisitedSitesTime(sitesTime);
        return userRepository.save(user);
    }

    private void generateAndSendSecurityCode(User user) {
        user.setSecurityCode(CodeGenerator.generateCode());
        user.setConfirmCode(true);
        emailService.sendSecurityCode(user.getEmail(), user.getFirstName(), user.getSecurityCode());
    }

}
