package it.xtreamdev.gflbe.dto.user;

import it.xtreamdev.gflbe.model.enumerations.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditUserDTO {

    private String fullname;
    private String email;
    private String mobilePhone;
    private String password;
    private RoleName role;

    private String editorInfo;
    private String editorInfoRemuneration;
    private String editorInfoNotes;

    private String companyName;
    private String url;
    private String companyDimension;
    private String businessArea;
    private String address;

    private String principalDomain;
    private String competitor1;
    private String competitor2;


}
