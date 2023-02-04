package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.common.PaginationRequest;
import it.xtreamdev.gflbe.dto.domain.SaveDomainDto;
import it.xtreamdev.gflbe.dto.domain.SearchDomainDto;
import it.xtreamdev.gflbe.model.Domain;
import it.xtreamdev.gflbe.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/domain")
public class DomainController {

    @Autowired
    private DomainService domainService;

    @GetMapping
    public Page<Domain> findAll(SearchDomainDto searchDomainDto, PaginationRequest paginationRequest) {
        return this.domainService.find(searchDomainDto, paginationRequest.getPageRequest());
    }

    @GetMapping("{id}")
    public Domain findOne(@PathVariable Integer id) {
        return this.domainService.findById(id);
    }

    @PostMapping
    public Domain save(@RequestBody SaveDomainDto saveDomainDto) {
        return this.domainService.save(saveDomainDto);
    }

    @PutMapping("{id}")
    public Domain save(@PathVariable Integer id, @RequestBody SaveDomainDto saveDomainDto) {
        return this.domainService.update(id, saveDomainDto);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        this.domainService.delete(id);
    }

}
