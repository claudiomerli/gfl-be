package it.xtreamdev.gflbe.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveCustomerDTO {

    @NotBlank
    private String username;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=.])(.{8,})$")
    private String password;
    @NotBlank
    private String companyName;
    @NotBlank
    private String url;
    @NotBlank
    private String companyDimension;
    @NotBlank
    private String businessArea;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String mobile;
    @NotBlank
    private String address;

}
