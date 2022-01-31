package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.FinanceDTO;
import it.xtreamdev.gflbe.dto.PageDTO;
import it.xtreamdev.gflbe.dto.PageableDTO;
import it.xtreamdev.gflbe.dto.SelectDTO;
import it.xtreamdev.gflbe.dto.newspaper.NewspaperDTO;
import it.xtreamdev.gflbe.dto.newspaper.SaveNewspaperDTO;
import it.xtreamdev.gflbe.dto.newspaper.SearchNewspaperDTO;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NewspaperService {

    @Autowired
    private NewspaperRepository newspaperRepository;
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

        return newspaperMapper.mapEntityToPageDTO(newspapers);
    }

    public PageDTO<?> findAll(SearchNewspaperDTO searchNewspaperDTO, PageRequest pageRequest) {
        Page<Newspaper> newspapers = this.newspaperRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Order> orderList = new ArrayList<>();
            orderList.add(criteriaBuilder.desc(root.get("id")));
            criteriaQuery.orderBy(orderList);
            List<Predicate> predicates = new ArrayList<>();
            if(isValidId(searchNewspaperDTO.getNewspaperId())) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchNewspaperDTO.getNewspaperId()));
            }
            if(isValidId(searchNewspaperDTO.getTopicId())) {
                predicates.add(criteriaBuilder.equal(root.join("topics").get("id"), searchNewspaperDTO.getTopicId()));
            }
            Optional.ofNullable(searchNewspaperDTO.getZaFrom()).ifPresent(zaFrom -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("za"), zaFrom)));
            Optional.ofNullable(searchNewspaperDTO.getZaTo()).ifPresent(zaTo -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("za"), zaTo)));
            Optional.ofNullable(searchNewspaperDTO.getPurchasedContentFrom()).ifPresent(purchasedContentFrom -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("purchasedContent"), purchasedContentFrom)));
            Optional.ofNullable(searchNewspaperDTO.getPurchasedContentTo()).ifPresent(purchasedContentTo -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("purchasedContent"), purchasedContentTo)));
            Optional.ofNullable(searchNewspaperDTO.getCostEachFrom()).ifPresent(costEachFrom -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("costEach"), costEachFrom)));
            Optional.ofNullable(searchNewspaperDTO.getCostEachTo()).ifPresent(costEachTo -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("costEach"), costEachTo)));
            Optional.ofNullable(searchNewspaperDTO.getCostSellFrom()).ifPresent(costSellFrom -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("costSell"), costSellFrom)));
            Optional.ofNullable(searchNewspaperDTO.getCostSellTo()).ifPresent(costSellTo -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("costSell"), costSellTo)));
            if(!StringUtils.isEmpty(searchNewspaperDTO.getName())) { predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + searchNewspaperDTO.getName().toUpperCase() + "%")); }
            if(!StringUtils.isEmpty(searchNewspaperDTO.getRegionalGeolocalization())) { predicates.add(criteriaBuilder.equal(criteriaBuilder.upper(root.get("regionalGeolocalization")), searchNewspaperDTO.getRegionalGeolocalization().toUpperCase())); }
            Optional.ofNullable(searchNewspaperDTO.getTopics()).ifPresent(topics -> {
                CriteriaBuilder.In<Integer> inClause = criteriaBuilder.in(root.join("topics").get("id"));
                topics.forEach(topic -> inClause.value(Integer.valueOf(topic)));
                predicates.add(inClause);
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);

        return newspaperMapper.mapEntityToPageDTO(newspapers);
    }

    public List<SelectDTO> findForSelect() {
        return newspaperMapper.mapEntityToSelectDTO(this.newspaperRepository.findAll());
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

    private boolean isValidId(Integer id) {
        return id !=null && id != 0;
    }
    private boolean isValidId(Double id) {
        return id !=null && id != 0;
    }
    private boolean isValidId(Long id) {
        return id !=null && id != 0;
    }
}
