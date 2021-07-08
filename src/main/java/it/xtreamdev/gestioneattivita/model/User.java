package it.xtreamdev.gestioneattivita.model;

import it.xtreamdev.gestioneattivita.model.enumerations.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
