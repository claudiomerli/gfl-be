package it.xtreamdev.gflbe.dto.message;

import it.xtreamdev.gflbe.model.enumerations.MessageTopicType;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveMessageDTO {

    private String message;
    private RoleName targetRole;
    private Integer targetUserId;
    private String topicId;
    private MessageTopicType topicType;
}
