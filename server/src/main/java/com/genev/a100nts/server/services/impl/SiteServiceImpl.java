package com.genev.a100nts.server.services.impl;

import com.genev.a100nts.server.dto.CommentDTO;
import com.genev.a100nts.server.exceptions.ClientException;
import com.genev.a100nts.server.models.*;
import com.genev.a100nts.server.repositories.SiteRepository;
import com.genev.a100nts.server.repositories.UserRepository;
import com.genev.a100nts.server.services.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.genev.a100nts.server.security.SecurityConfiguration.getCurrentUserEmail;

@Service
public class SiteServiceImpl implements SiteService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SiteRepository siteRepository;

    @Override
    public Site add(Site site) {
        List<SiteImage> siteImages = site.getImagesUI().stream().map(l -> new SiteImage(site, l)).collect(Collectors.toList());
        site.setImages(siteImages);
        site.setVotes(Collections.emptyList());
        site.setComments(Collections.emptyList());
        site.setFavouriteOfUsers(Collections.emptyList());
        site.setVisitedByUsers(Collections.emptyList());
        return siteRepository.save(site);
    }

    @Override
    public List<Site> getAll() {
        return siteRepository.findAll();
    }

    @Override
    public List<Site> getAllVisible() {
        return siteRepository.findAll().stream().filter(Site::isVisible).collect(Collectors.toList());
    }

    @Override
    public Site getDetails(Long id) {
        return siteRepository.findById(id).orElse(null);
    }

    @Override
    public List<Site> getVisibleForVisiting() {
        User user = userRepository.findByEmail(getCurrentUserEmail()).get();
        return siteRepository.findAll().stream()
                .filter(Site::isVisible)
                .filter(s -> user.getVisitedSites().stream().noneMatch(userSite -> userSite.getId().equals(s.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Site update(Site site) {
        Site siteDB = siteRepository.findById(site.getId()).get();
        siteDB.setTitle(site.getTitle());
        siteDB.setTitleBG(site.getTitleBG());
        siteDB.setProvince(site.getProvince());
        siteDB.setProvinceBG(site.getProvinceBG());
        siteDB.setTown(site.getTown());
        siteDB.setTownBG(site.getTownBG());
        siteDB.setDescription(site.getDescription());
        siteDB.setDescriptionBG(site.getDescriptionBG());
        siteDB.setLatitude(site.getLatitude());
        siteDB.setLongitude(site.getLongitude());
        siteDB.setVisible(site.isVisible());
        List<String> imagesUI = site.getImagesUI();
        List<SiteImage> images = new ArrayList<>(siteDB.getImages());
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setSrc(imagesUI.get(i));
        }
        siteDB.setImages(images);
        return siteRepository.save(siteDB);
    }

    @Override
    @Transactional
    public Site vote(com.genev.a100nts.server.web.Vote vote) {
        Site site = siteRepository.findById(vote.getSiteId()).get();
        User user = userRepository.findByEmail(getCurrentUserEmail()).get();
        if (site.getVisitedByUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            throw new ClientException(String.format("User with ID %d has not visited site " +
                    "with ID %d to be able to rate it", user.getId(), site.getId()));
        }

        Vote voteDb = site.getVotes().stream()
                .filter(v -> v.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);
        List<Vote> siteVotes = new ArrayList<>(site.getVotes());
        if (voteDb != null) {
            int voteIndex = site.getVotes().indexOf(voteDb);
            voteDb.setVote(vote.getVote());
            siteVotes.set(voteIndex, voteDb);
        } else {
            siteVotes.add(new Vote(
                    site, user, vote.getVote()
            ));
        }
        site.setVotes(siteVotes);
        return siteRepository.save(site);
    }

    @Override
    public Integer getUserVote(Long siteId) {
        User user = userRepository.findByEmail(getCurrentUserEmail()).get();
        return siteRepository.findById(siteId).get().getVotes().stream()
                .filter(v -> v.getUser().getId().equals(user.getId()))
                .map(Vote::getVote)
                .findFirst()
                .orElse(0);
    }

    @Override
    @Transactional
    public Comment addComment(CommentDTO comment) {
        Site site = siteRepository.findById(comment.getSiteId()).get();
        User user = userRepository.findByEmail(getCurrentUserEmail()).get();
        List<Comment> comments = new ArrayList<>(site.getComments());
        Comment newComment = new Comment(
                user, site, comment.getComment(), comment.getDateTime()
        );
        comments.add(newComment);
        site.setComments(comments);
        return newComment;
    }

}
