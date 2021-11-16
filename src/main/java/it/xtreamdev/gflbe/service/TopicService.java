package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.topic.SaveTopicDTO;
import it.xtreamdev.gflbe.dto.topic.TopicDTO;
import it.xtreamdev.gflbe.model.Topic;
import it.xtreamdev.gflbe.repository.TopicRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    public Page<Topic> findAll(String globalSearch, PageRequest pageRequest) {
        return this.topicRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(globalSearch)) {
                Arrays.asList(globalSearch.split(" ")).forEach(searchPortion ->
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.upper(root.get("topic_name")), "%" + searchPortion.toUpperCase() + "%")
                        ))
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);
    }

    public Set<TopicDTO> findAll() {
        return this.topicRepository.findAll().stream()
                .map(topic -> TopicDTO
                        .builder()
                        .id(topic.getId())
                        .name(topic.getName())
                        .build())
                .collect(Collectors.toSet());
    }

    public void save(SaveTopicDTO saveTopicDTO) {
        this.topicRepository.save(
                Topic
                    .builder()
                    .name(saveTopicDTO.getName())
                    .build()
        );
    }

    public void delete(Integer id) {
        this.topicRepository.deleteById(id);
    }

    public void update(Integer id, SaveTopicDTO saveTopicDTO) {
        Topic persistedTopic = this.findById(id);
        persistedTopic.setName(saveTopicDTO.getName());
        this.topicRepository.save(persistedTopic);
    }

    public Topic findById(Integer id) {
        return this.topicRepository
                .findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Topic not found"));
    }
}
