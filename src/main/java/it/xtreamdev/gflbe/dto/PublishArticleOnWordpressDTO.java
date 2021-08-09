package it.xtreamdev.gflbe.dto;

import it.xtreamdev.gflbe.dto.enumerations.PublishArticleStatusEnum;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class PublishArticleOnWordpressDTO {
    @NotNull(message = "siteUrl must not be null")
    private String siteUrl;
    @NotNull(message = "username must not be null")
    private String username;
    @NotNull(message = "password must not be null")
    private String password;
    @NotNull(message = "title must not be null")
    private String title;
    @NotNull(message = "content must not be null")
    private String content;
    @Builder.Default
    private PublishArticleStatusEnum status = PublishArticleStatusEnum.DRAFT;
    private String categories;
    private String tags;
    private LocalDateTime date;
    private String excerpt;
}
