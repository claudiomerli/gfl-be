package it.xtreamdev.gflbe.dto;

import it.xtreamdev.gflbe.model.enumerations.ContentProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangeProjectStatusContentDTO {
    @NotNull
    private ContentProjectStatus status;
}
