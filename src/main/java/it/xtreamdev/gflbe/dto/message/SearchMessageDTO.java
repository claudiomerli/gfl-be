package it.xtreamdev.gflbe.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchMessageDTO {

    private String participant1Role;
    private Integer participant1UserId;
    private String participant2Role;
    private Integer participant2UserId;
    private String topicId;
    private String topicType;

}
