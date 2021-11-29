package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateProjectDTO {
    private String name;
    private Integer customerId;
    private List<SaveProjectContentPreviewDTO> projectContentPreviews;
}
