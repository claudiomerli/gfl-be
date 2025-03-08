package it.xtreamdev.gflbe.dto.newspaper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveNewspaperDescriptionDTO {

    private String description;

}
