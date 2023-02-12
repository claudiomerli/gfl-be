package it.xtreamdev.gflbe.dto.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveDomainDto {

    private String name;

    private String wordpressUsername;

    private String wordpressPassword;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiration;

    private String ip;

    private Integer hostingId;

    private Integer newspaperId;

}
