package it.xtreamdev.gflbe.dto;

import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangeStatusProjectDTO {
    @NotNull
    private ProjectStatus status;
}
