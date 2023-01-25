package it.xtreamdev.gflbe.dto.project;


import com.fasterxml.jackson.annotation.JsonFormat;
import it.xtreamdev.gflbe.model.ProjectCommission;
import it.xtreamdev.gflbe.model.ProjectStatusChange;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.ProjectCommissionStatus;
import it.xtreamdev.gflbe.model.enumerations.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProjectListElementDTO {

    private Integer id;
    private String name;
    private User customer;
    private String billingDescription;
    private Double billingAmount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiration;
    private ProjectStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime lastModifiedDate;

    private List<ProjectCommissionListElementDTO> projectCommissions;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class ProjectCommissionListElementDTO {

        private ProjectCommissionStatus status;

    }
}
