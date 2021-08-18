package it.xtreamdev.gflbe.dto.editor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditEditorDTO {

    private String fullname;
    private String email;
    private String mobilePhone;
    private String level;
    private String remuneration;
    private String password;

}
