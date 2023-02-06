package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.domain.SaveDomainDto;
import it.xtreamdev.gflbe.dto.domain.SearchDomainDto;
import it.xtreamdev.gflbe.model.Domain;
import it.xtreamdev.gflbe.model.Hosting;
import it.xtreamdev.gflbe.repository.DomainRepository;
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
public class DomainService {

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private HostingRepository hostingRepository;

    @Autowired
    private ProjectService projectService;

    public Page<Domain> find(SearchDomainDto searchDomainDto, Pageable pageable) {
        return this.domainRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(searchDomainDto.getGlobalSearch())) {
                Arrays.asList(searchDomainDto.getGlobalSearch().split(" ")).forEach(searchPortion ->
                        predicates.add(criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + searchPortion.toUpperCase() + "%"),
                                criteriaBuilder.like(criteriaBuilder.upper(root.get("ip")), "%" + searchPortion.toUpperCase() + "%")
                        ))
                );
            }

            if (searchDomainDto.getExpirationFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expiration"), searchDomainDto.getExpirationFrom()));
            }

            if (searchDomainDto.getExpirationTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expiration"), searchDomainDto.getExpirationTo()));
            }

            if (searchDomainDto.getHostingId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("hosting").get("id"), searchDomainDto.getHostingId()));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, pageable);
    }

    public Domain findById(Integer id) {
        return this.domainRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Entity not found"));
    }

    public Domain save(SaveDomainDto saveDomainDto) {
        Hosting hosting = this.hostingRepository.findById(saveDomainDto.getHostingId()).orElseThrow();
        Domain domain = this.domainRepository.save(Domain.builder()
                .name(saveDomainDto.getName())
                .wordpressUsername(saveDomainDto.getWordpressUsername())
                .wordpressPassword(saveDomainDto.getWordpressPassword())
                .expiration(saveDomainDto.getExpiration())
                .ip(saveDomainDto.getIp())
                .hosting(hosting)
                .build());
        projectService.createProjectFromDomain(domain);
        return domain;
    }


    public Domain update(Integer id, SaveDomainDto saveDomainDto) {
        Hosting hosting = this.hostingRepository.findById(saveDomainDto.getHostingId()).orElseThrow();
        Domain domain = this.findById(id);
        domain.setName(saveDomainDto.getName());
        domain.setWordpressUsername(saveDomainDto.getWordpressUsername());
        domain.setWordpressPassword(saveDomainDto.getWordpressPassword());
        domain.setExpiration(saveDomainDto.getExpiration());
        domain.setIp(saveDomainDto.getIp());
        domain.setHosting(hosting);
        return this.domainRepository.save(domain);
    }

    public void delete(Integer id) {
        Domain domain = this.findById(id);
        this.projectService.removeReferenceFromDomain(domain);

        domain.setDeleted(true);
        this.domainRepository.save(domain);
    }

}
