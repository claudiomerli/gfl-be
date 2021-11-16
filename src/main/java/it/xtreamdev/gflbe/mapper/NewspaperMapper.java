package it.xtreamdev.gflbe.mapper;

import it.xtreamdev.gflbe.dto.newspaper.NewspaperDTO;
import it.xtreamdev.gflbe.dto.topic.TopicDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class NewspaperMapper {

    public NewspaperDTO mapEntityToDTO(Newspaper newspaper) {

        return NewspaperDTO
                .builder()
                .id(newspaper.getId())
                .costEach(newspaper.getCostEach())
                .costSell(newspaper.getCostSell())
                .name(newspaper.getName())
                .email(newspaper.getEmail())
                .purchasedContent(newspaper.getPurchasedContent())
                .regionalGeolocalization(newspaper.getRegionalGeolocalization())
                .note(newspaper.getNote())
                .topics(newspaper.getTopics().stream().map(topic -> TopicDTO
                                .builder()
                                .id(topic.getId())
                                .name(topic.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

}
