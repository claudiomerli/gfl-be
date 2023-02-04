package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.common.PaginationRequest;
import it.xtreamdev.gflbe.dto.hosting.SaveHostingDto;
import it.xtreamdev.gflbe.model.Hosting;
import it.xtreamdev.gflbe.service.HostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/hosting")
public class HostingController {

    @Autowired
    private HostingService hostingService;

    @GetMapping
    public Page<Hosting> findAll(@RequestParam(value = "globalSearch", required = false) String globalSearch, PaginationRequest paginationRequest) {
        return this.hostingService.find(globalSearch, paginationRequest.getPageRequest());
    }

    @GetMapping("{id}")
    public Hosting findOne(@PathVariable Integer id) {
        return this.hostingService.findById(id);
    }

    @PostMapping
    public Hosting save(@RequestBody SaveHostingDto saveHostingDto) {
        return this.hostingService.save(saveHostingDto);
    }

    @PutMapping("{id}")
    public Hosting save(@PathVariable Integer id, @RequestBody SaveHostingDto saveHostingDto) {
        return this.hostingService.update(id, saveHostingDto);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        this.hostingService.delete(id);
    }

}
