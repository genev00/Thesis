package com.genev.a100nts.server.models;

import com.genev.a100nts.server.utils.ListToStringConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UserRole role;

    @NotNull(groups = {Register.class, Update.class})
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]{3,255}$", groups = {Register.class, Update.class})
    private String firstName;

    @NotNull(groups = {Register.class, Update.class})
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]{3,255}$", groups = {Register.class, Update.class})
    private String lastName;

    @NotNull(groups = {Register.class, Login.class, Update.class, ResetPass.class})
    @Email(groups = {Register.class, Login.class, Update.class, ResetPass.class})
    private String email;

    @NotNull(groups = {Register.class, Login.class, ResetPass.class})
    @Pattern(regexp = "^(?=[^A-Z\\n]*[A-Z])(?=[^a-z\\n]*[a-z])(?=[^0-9\\n]*[0-9])(?=[^#?!@$%^&*\\n-]*[#?!@$%^&*-]).{8,50}$", groups = {Register.class, Login.class, Update.class, ResetPass.class})
    private String password;

    private boolean ranking;

    private boolean confirmCode;

    @Null(groups = Register.class)
    private String securityCode;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Null(groups = Register.class)
    private List<Vote> votes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Null(groups = Register.class)
    private List<Comment> comments;

    @JoinTable(
            name = "favourite_sites_of_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "site_id")
    )
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @Null(groups = Register.class)
    private List<Site> favouriteSites;

    @JoinTable(
            name = "visited_sites_by_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "site_id")
    )
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @Null(groups = Register.class)
    private List<Site> visitedSites;

    @Convert(converter = ListToStringConverter.class)
    @Null(groups = Register.class)
    private List<String> visitedSitesTime;

    public interface Register {
    }

    public interface Login {
    }

    public interface Update {
    }

    public interface ResetPass {
    }

}
