package it.xtreamdev.gflbe.mapper;

import it.xtreamdev.gflbe.dto.PageDTO;
import it.xtreamdev.gflbe.dto.PageableDTO;
import it.xtreamdev.gflbe.dto.SelectDTO;
import it.xtreamdev.gflbe.dto.newspaper.NewspaperDTO;
import it.xtreamdev.gflbe.dto.topic.TopicDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NewspaperMapper {

    @Autowired
    NewspaperMapper newspaperMapper;
    @Autowired
    ContentRepository contentRepository;

    public List<SelectDTO> mapEntityToSelectDTO(List<Newspaper> newspapers) {

        return newspapers.stream().map(newspaper -> SelectDTO.builder()
                        .id(newspaper.getId())
                        .descrizione(newspaper.getName())
                        .build()).collect(Collectors.toList());
    }

    public PageDTO<Object> mapEntityToPageDTO(Page<Newspaper> newspapers) {
        return PageDTO.builder()
                .pageable(PageableDTO
                        .builder()
                        .pageSize(newspapers.getPageable().getPageSize())
                        .pageNumber(newspapers.getPageable().getPageNumber())
                        .build())
                .totalElements(newspapers.getTotalElements())
                .content(newspapers.get()
                        .map(newspaper -> {
                            NewspaperDTO dto = newspaperMapper.mapEntityToDTO(newspaper);
                            dto.setLeftContent(dto.getPurchasedContent() - contentRepository.countByNewspaper_Id(dto.getId()));
                            return dto;
                        })
                        .collect(Collectors.toList()))
                .build();
    }

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
                .za(newspaper.getZa())
                .ip(newspaper.getIp())
                .topics(newspaper.getTopics().stream().map(topic -> TopicDTO
                                .builder()
                                .id(topic.getId())
                                .name(topic.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

}
