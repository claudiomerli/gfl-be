package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.FinanceDTO;
import it.xtreamdev.gflbe.dto.PageDTO;
import it.xtreamdev.gflbe.dto.PageableDTO;
import it.xtreamdev.gflbe.dto.newspaper.NewspaperDTO;
import it.xtreamdev.gflbe.dto.newspaper.SaveNewspaperDTO;
import it.xtreamdev.gflbe.mapper.NewspaperMapper;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.model.Topic;
import it.xtreamdev.gflbe.repository.ContentRepository;
import it.xtreamdev.gflbe.repository.NewspaperRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewspaperService {

    @Autowired
    private NewspaperRepository newspaperRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private NewspaperMapper newspaperMapper;

    public PageDTO<?> findAll(String globalSearch, PageRequest pageRequest) {
        Page<Newspaper> newspapers = this.newspaperRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Order> orderList = new ArrayList<>();
            orderList.add(criteriaBuilder.desc(root.get("id")));
            criteriaQuery.orderBy(orderList);
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(globalSearch)) {
                Arrays.asList(globalSearch.split(" ")).forEach(searchPortion ->
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + searchPortion.toUpperCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.upper(root.get("email")), "%" + searchPortion.toUpperCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.upper(root.get("regionalGeolocalization")), "%" + searchPortion.toUpperCase() + "%")
                        ))
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);


        return PageDTO
                .builder()
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
                        .collect(Collectors.toSet()))
                .build();
    }

    public void save(SaveNewspaperDTO newspaper) {
        this.newspaperRepository.save(
                Newspaper
                        .builder()
                        .name(newspaper.getName())
                        .costEach(newspaper.getCostEach())
                        .costSell(newspaper.getCostSell())
                        .email(newspaper.getEmail())
                        .purchasedContent(newspaper.getPurchasedContent())
                        .regionalGeolocalization(newspaper.getRegionalGeolocalization())
                        .note(newspaper.getNote())
                        .topics(newspaper.getTopics().stream().map(topicId -> Topic.builder().id(topicId).build()).collect(Collectors.toSet()))
                        .build()
        );
    }

    public void delete(Integer id) {
        this.newspaperRepository.deleteById(id);
    }

    public void update(Integer id, SaveNewspaperDTO saveNewspaperDTO) {
        Newspaper persistedNewspaper = this.findById(id);

        persistedNewspaper.setName(saveNewspaperDTO.getName());
        persistedNewspaper.setEmail(saveNewspaperDTO.getEmail());
        persistedNewspaper.setCostEach(saveNewspaperDTO.getCostEach());
        persistedNewspaper.setCostSell(saveNewspaperDTO.getCostSell());
        persistedNewspaper.setPurchasedContent(saveNewspaperDTO.getPurchasedContent());
        persistedNewspaper.setRegionalGeolocalization(saveNewspaperDTO.getRegionalGeolocalization());
        persistedNewspaper.setNote(saveNewspaperDTO.getNote());
        persistedNewspaper.setTopics(saveNewspaperDTO.getTopics().stream().map(topicId -> Topic.builder().id(topicId).build()).collect(Collectors.toSet()));

        this.newspaperRepository.save(persistedNewspaper);
    }

    public NewspaperDTO detail(Integer id) {
        Newspaper newspaper = this.newspaperRepository
                .findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Newspaper not found"));
        return newspaperMapper.mapEntityToDTO(newspaper);
    }

    private Newspaper findById(Integer id) {
        return this.newspaperRepository
                .findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Newspaper not found"));
    }

    public FinanceDTO finance() {
        return this.newspaperRepository.finance();
    }
}
