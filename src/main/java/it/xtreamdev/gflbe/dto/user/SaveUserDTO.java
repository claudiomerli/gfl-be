package it.xtreamdev.gflbe.dto.user;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveUserDTO extends EditUserDTO {

    private String username;

}
