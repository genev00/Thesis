package com.genev.a100nts.server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @Column(length = 300)
    private String comment;

    private LocalDateTime dateTime;

    public Comment(User user, Site site, String comment, LocalDateTime dateTime) {
        this.user = user;
        this.site = site;
        this.comment = comment;
        this.dateTime = dateTime;
    }

}
