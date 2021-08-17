package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveEditorDTO {

    private String username;
    private String fullname;
    private String email;
    private String mobilephone;
    private String level;
    private String remuneration;
    private String password;

}
