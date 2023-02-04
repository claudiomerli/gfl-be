package it.xtreamdev.gflbe.dto.hosting;

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
public class SaveHostingDto {

    private String name;

    private String url;

    private String username;

    private String password;

    private String notes;


}
