package it.xtreamdev.gflbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(name = "username")
    private String username;

    @NotBlank
    @Column(name = "fullname")
    private String fullname;

    @NotBlank
    @Column(name = "email")
    private String email;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "password")
    private String password;

    @OneToOne(mappedBy = "editor", cascade = CascadeType.ALL)
    private EditorInfo editorInfo;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private CustomerInfo customerInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleName role;

    @ManyToOne
    @JsonIgnore
    private User agency;
    @OneToMany(mappedBy = "agency")
    private List<User> finalCustomers;

    @JsonIgnore
    private String activationCode;

    @JsonIgnore
    @Builder.Default
    private Boolean active = false;

    @Builder.Default
    private Boolean emailVerified = true;

    @JsonIgnore
    private String emailVerificationCode;

    @JsonIgnore
    private String resetPasswordCode;

    public User cleanSensitiveData() {
        this.password = null;
        return this;
    }

}
