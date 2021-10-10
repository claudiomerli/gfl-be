package it.xtreamdev.gflbe.model;

import it.xtreamdev.gflbe.model.enumerations.RoleName;
import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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

    @Column(name = "level")
    private String level;

    @Column(name = "remuneration")
    private String remuneration;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleName role;

    @Formula("(select avg(c.score) from content c where c.editor_id = id)")
    private Double averageScore;


}
