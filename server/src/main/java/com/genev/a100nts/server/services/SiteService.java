package com.genev.a100nts.server.services;

import com.genev.a100nts.server.dto.CommentDTO;
import com.genev.a100nts.server.models.Comment;
import com.genev.a100nts.server.models.Site;
import com.genev.a100nts.server.web.Vote;

import java.util.List;

public interface SiteService {

    Site add(Site site);

    List<Site> getAll();

    List<Site> getAllVisible();

    Site getDetails(Long id);

    List<Site> getVisibleForVisiting();

    Site update(Site site);

    Site vote(Vote vote);

    Integer getUserVote(Long siteId);

    Comment addComment(CommentDTO comment);

}
