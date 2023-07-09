package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.mejestic.FullDomainSeoStatistic;
import it.xtreamdev.gflbe.service.MajesticSEOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/statistics")
public class StatisticsController {

    @Autowired
    private MajesticSEOService majesticSEOService;

    @GetMapping
    public FullDomainSeoStatistic getStatistics() {
        return this.majesticSEOService.getCustomerStatistics();
    }

}
