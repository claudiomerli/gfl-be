package it.xtreamdev.gflbe.mapper;

import it.xtreamdev.gflbe.dto.newspaper.NewspaperDTO;
import it.xtreamdev.gflbe.dto.topic.TopicDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class NewspaperMapper {



    public Page<NewspaperDTO> mapEntityToDTO(Page<Newspaper> newspapers) {
        return newspapers.map(this::mapEntityToDTO);
    }

    public NewspaperDTO mapEntityToDTO(Newspaper newspaper) {
        return NewspaperDTO
                .builder()
                .id(newspaper.getId())
                .costEach(newspaper.getCostEach())
                .costSell(newspaper.getCostSell())
                .leftContent(newspaper.getLeftContent())
                .name(newspaper.getName())
                .email(newspaper.getEmail())
                .regionalGeolocalization(newspaper.getRegionalGeolocalization())
                .note(newspaper.getNote())
                .za(newspaper.getZa())
                .ip(newspaper.getIp())
                .topics(newspaper.getTopics().stream().map(topic -> TopicDTO
                                .builder()
                                .id(topic.getId())
                                .name(topic.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .hidden(newspaper.getHidden())
                .sensitiveTopics(newspaper.getSensitiveTopics())
                .warning(newspaper.getWarning())
                .build();
    }

}
