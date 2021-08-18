package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.newspaper.SaveNewspaperDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.repository.NewspaperRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NewspaperService {

    @Autowired
    private NewspaperRepository newspaperRepository;

    public List<Newspaper> findAll(String globalSearch) {
        return this.newspaperRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(globalSearch)) {
                Arrays.asList(globalSearch.split(" ")).forEach(searchPortion -> {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + searchPortion.toUpperCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("email")), "%" + searchPortion.toUpperCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.upper(root.get("regionalGeolocalization")), "%" + searchPortion.toUpperCase() + "%")
                    ));
                });
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public void save(SaveNewspaperDTO newspaper) {
        this.newspaperRepository.save(
                Newspaper
                        .builder()
                        .name(newspaper.getName())
                        .costEach(newspaper.getCostEach())
                        .email(newspaper.getEmail())
                        .purchasedContent(newspaper.getPurchasedContent())
                        .regionalGeolocalization(newspaper.getRegionalGeolocalization())
                        .topic(newspaper.getTopic())
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
        persistedNewspaper.setPurchasedContent(saveNewspaperDTO.getPurchasedContent());
        persistedNewspaper.setRegionalGeolocalization(saveNewspaperDTO.getRegionalGeolocalization());
        persistedNewspaper.setTopic(saveNewspaperDTO.getTopic());

        this.newspaperRepository.save(persistedNewspaper);
    }

    public Newspaper findById(Integer id) {
        return this.newspaperRepository
                .findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Newspaper not found"));
    }
}
