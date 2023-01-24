package it.xtreamdev.gflbe.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SaveAttachmentDTO {

    private String filename;
    private String body;

}
