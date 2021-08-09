package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AdminStatusDTO {

    private Long contentCount;

    private Long editorCount;

    private Long customersCount;

}
