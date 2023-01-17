package it.xtreamdev.gflbe.dto.content;

import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SaveContentDTO {

    private String body;

}
