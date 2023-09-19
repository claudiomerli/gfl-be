package it.xtreamdev.gflbe.dto.majestic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkCheckDTO {

    private String publicationUrl;
    private String url;
    private String anchor;
    private Boolean isOnline;
    private Boolean isInIndex;
    private Boolean containsUrl;
    private Boolean containsCorrectAnchorText;
    private Boolean isFollow;

}
