package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.hosting.SaveHostingDto;
import it.xtreamdev.gflbe.model.Hosting;
import it.xtreamdev.gflbe.repository.HostingRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HostingService {

    @Autowired
    private HostingRepository hostingRepository;

    public Page<Hosting> find(String globalSearch, Pageable pageable) {
        return this.hostingRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(globalSearch)) {
                Arrays.asList(globalSearch.split(" ")).forEach(searchPortion ->
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + searchPortion.toUpperCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.upper(root.get("url")), "%" + searchPortion.toUpperCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.upper(root.get("username")), "%" + searchPortion.toUpperCase() + "%")
                        ))
                );
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, pageable);
    }

    public Hosting findById(Integer id) {
        return this.hostingRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Entity not found"));
    }

    public Hosting save(SaveHostingDto saveHostingDto) {
        return this.hostingRepository.save(Hosting.builder()
                .name(saveHostingDto.getName())
                .url(saveHostingDto.getUrl())
                .username(saveHostingDto.getUsername())
                .password(saveHostingDto.getPassword())
                .notes(saveHostingDto.getNotes()).build());
    }


    public Hosting update(Integer id, SaveHostingDto saveHostingDto) {
        Hosting hosting = this.findById(id);
        hosting.setName(saveHostingDto.getName());
        hosting.setUrl(saveHostingDto.getUrl());
        hosting.setUsername(saveHostingDto.getUsername());
        hosting.setPassword(saveHostingDto.getPassword());
        hosting.setNotes(saveHostingDto.getNotes());
        return this.hostingRepository.save(hosting);
    }

    public void delete(Integer id) {
        Hosting hosting = this.findById(id);
        hosting.setDeleted(true);
        this.hostingRepository.save(hosting);
    }

}
