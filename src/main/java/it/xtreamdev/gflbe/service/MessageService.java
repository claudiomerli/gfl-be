package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.message.SaveMessageDTO;
import it.xtreamdev.gflbe.dto.message.SearchMessageDTO;
import it.xtreamdev.gflbe.model.Message;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    public Message saveMessage(SaveMessageDTO saveMessageDTO) {
        User user = userService.userInfo();

        Message message = new Message();
        message.setMessage(saveMessageDTO.getMessage());
        message.setTargetRole(saveMessageDTO.getTargetRole());
        message.setTargetUser(Optional.ofNullable(saveMessageDTO.getTargetUserId()).map(integer -> userService.findById(integer)).orElse(null));
        message.setTopicId(saveMessageDTO.getTopicId());
        message.setTopicType(saveMessageDTO.getTopicType());

        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        message.setSourceRole(user.getRole());
        message.setSourceUser(user);

        return messageRepository.save(message);
    }

    public Page<Message> find(SearchMessageDTO searchMessageDTO, Pageable pageable) {
        return this.messageRepository.findAll(((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (searchMessageDTO.getTopicId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("topicId"), searchMessageDTO.getTopicId()));
            }
            if (searchMessageDTO.getTopicType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("topicType"), searchMessageDTO.getTopicType()));
            }

            if (searchMessageDTO.getParticipant1Role() != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("targetRole"), RoleName.valueOf(searchMessageDTO.getParticipant1Role())),
                        criteriaBuilder.equal(root.get("sourceRole"), RoleName.valueOf(searchMessageDTO.getParticipant1Role()))
                ));
            }

            if (searchMessageDTO.getParticipant2Role() != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("targetRole"), RoleName.valueOf(searchMessageDTO.getParticipant2Role())),
                        criteriaBuilder.equal(root.get("sourceRole"), RoleName.valueOf(searchMessageDTO.getParticipant2Role()))
                ));
            }

            if (searchMessageDTO.getParticipant1UserId() != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("sourceUser"), userService.findById(searchMessageDTO.getParticipant1UserId())),
                        criteriaBuilder.equal(root.get("targetUser"), userService.findById(searchMessageDTO.getParticipant1UserId()))
                ));
            }

            if (searchMessageDTO.getParticipant2UserId() != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("sourceUser"), userService.findById(searchMessageDTO.getParticipant2UserId())),
                        criteriaBuilder.equal(root.get("targetUser"), userService.findById(searchMessageDTO.getParticipant2UserId()))
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "timestamp"));
    }

    public Message read(Integer id) {
        Message message = this.messageRepository.findById(id).orElseThrow();
        message.setRead(true);
        return this.messageRepository.save(message);
    }
}
