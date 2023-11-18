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
public class SaveCustomerInfoDTO {

    @NotBlank
    private String companyName;
    @NotBlank
    private String companyDimension;
    @NotBlank
    private String businessArea;
    @NotBlank
    private String mobile;
    @NotBlank
    private String address;
    private String piva;
    private String logo;

}
