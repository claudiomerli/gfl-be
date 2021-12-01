package it.xtreamdev.gflbe.dto.user;

import it.xtreamdev.gflbe.model.enumerations.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditUserDTO {

    private String fullname;
    private String email;
    private String mobilePhone;
    private String level;
    private String remuneration;
    private String password;
    private RoleName role;

}
