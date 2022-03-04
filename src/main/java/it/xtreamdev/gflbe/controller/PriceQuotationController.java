package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.GeneratePriceQuotationPDFDto;
import it.xtreamdev.gflbe.service.PriceQuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/priceQuotation")
public class PriceQuotationController {


    @Autowired
    private PriceQuotationService priceQuotationService;

    @PostMapping("generatePDF")
    public ResponseEntity<byte[]> generatePDF(@RequestBody GeneratePriceQuotationPDFDto generatePriceQuotationPDFDto) throws IOException {
        return ResponseEntity.ok(this.priceQuotationService.generatePDF(generatePriceQuotationPDFDto));
    }

}
