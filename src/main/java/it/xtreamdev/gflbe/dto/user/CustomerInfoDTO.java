package it.xtreamdev.gflbe.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CustomerInfoDTO {

    private Integer id;
    private String companyName;
    private String address;
    private String piva;
    private String email;
    private String mobile;
    private String logo;

}
