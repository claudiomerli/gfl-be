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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @NotBlank
    private String username;

    @NotBlank
    private String fullname;

    @NotBlank
    private String email;

    private String mobilePhone;

    private String password;

    private String level;

    private String remuneration;

    @Formula("(select avg(c.score) from content c where c.editor_id = id)")
    private Double averageScore;

    @Enumerated(EnumType.STRING)
    private RoleName role;

}
