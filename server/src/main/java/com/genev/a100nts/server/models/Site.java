package com.genev.a100nts.server.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(groups = Update.class)
    private Long id;

    @NotNull(groups = {Add.class, Update.class})
    @Length(min = 3, max = 255, groups = {Add.class, Update.class})
    private String title;

    @NotNull(groups = {Add.class, Update.class})
    @Length(min = 3, max = 255, groups = {Add.class, Update.class})
    private String titleBG;

    @NotNull(groups = {Add.class, Update.class})
    @Length(min = 3, max = 255, groups = {Add.class, Update.class})
    private String province;

    @NotNull(groups = {Add.class, Update.class})
    @Length(min = 3, max = 255, groups = {Add.class, Update.class})
    private String provinceBG;

    @NotNull(groups = {Add.class, Update.class})
    @Length(min = 3, max = 255, groups = {Add.class, Update.class})
    private String town;

    @NotNull(groups = {Add.class, Update.class})
    @Length(min = 3, max = 255, groups = {Add.class, Update.class})
    private String townBG;

    @NotNull(groups = {Add.class, Update.class})
    @Length(min = 10, max = 2048, groups = {Add.class, Update.class})
    @Column(length = 2048)
    private String description;

    @NotNull(groups = {Add.class, Update.class})
    @Length(min = 10, max = 2048, groups = {Add.class, Update.class})
    @Column(length = 2048)
    private String descriptionBG;

    @NotNull(groups = {Add.class, Update.class})
    private String latitude;

    @NotNull(groups = {Add.class, Update.class})
    private String longitude;

    private boolean visible;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    @Null(groups = {Add.class, Update.class})
    private List<SiteImage> images;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    @Null(groups = {Add.class, Update.class})
    private List<Vote> votes;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    @Null(groups = {Add.class, Update.class})
    private List<Comment> comments;

    @JoinTable(
            name = "favourite_sites_of_user",
            joinColumns = @JoinColumn(name = "site_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ManyToMany
    @Null(groups = {Add.class, Update.class})
    private List<User> favouriteOfUsers;

    @JoinTable(
            name = "visited_sites_by_user",
            joinColumns = @JoinColumn(name = "site_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ManyToMany
    @Null(groups = {Add.class, Update.class})
    private List<User> visitedByUsers;

    @Transient
    @NotNull(groups = {Add.class, Update.class})
    @Size(min = 3, max = 3, groups = {Add.class, Update.class})
    private List<String> imagesUI;

    public interface Add {
    }

    public interface Update {
    }

}
