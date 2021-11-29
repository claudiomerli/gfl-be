package it.xtreamdev.gflbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveProjectContentPreviewDTO {

    private Integer id;
    private Integer newspaperId;
    private Month monthUse;
    private String linkUrl;
    private String linkText;
    private String title;
    private String customerNotes;

}
