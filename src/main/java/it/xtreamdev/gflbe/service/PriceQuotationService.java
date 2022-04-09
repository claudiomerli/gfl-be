package it.xtreamdev.gflbe.service;

import com.itextpdf.html2pdf.HtmlConverter;
import it.xtreamdev.gflbe.dto.GeneratePriceQuotationPDFDto;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.repository.NewspaperRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@CommonsLog
public class PriceQuotationService {

    @Autowired
    private NewspaperRepository newspaperRepository;

    public byte[] generatePDF(GeneratePriceQuotationPDFDto generatePriceQuotationPDFDto) throws IOException {
        Resource resource = new ClassPathResource("pdf-generation/price-quotation.html");
        FileInputStream fis = new FileInputStream(resource.getFile());
        String fileContent = IOUtils.toString(fis, StandardCharsets.UTF_8);
        double taxPercentage = 22.0;

        Double totalExpense = generatePriceQuotationPDFDto.getRows().stream().map(GeneratePriceQuotationPDFDto.PriceQuotationRow::getRevenue).reduce(0.0, Double::sum);
        Double taxExpense = generatePriceQuotationPDFDto.getOtherInformation().getTax() ? totalExpense*(taxPercentage/100) : 0;
        double total = totalExpense+taxExpense;

        String tableBody = generatePriceQuotationPDFDto.getRows().stream().map(priceQuotationRow -> {
            Newspaper newspaper = this.newspaperRepository.findById(priceQuotationRow.getId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Newspaper not found"));
            return "<tbody><tr style=\"outline: thin solid;\"><td style=\"border-right: thin solid; padding: 1em;\">" + newspaper.getName() + "</td><td style=\"border-right: thin solid; padding: 1em;\">" + newspaper.getZa() + "</td><td style=\"padding: 1em;\">" + newspaper.getIp() + "</td></tr></tbody>";
        }).collect(Collectors.joining());

        fileContent = fileContent
                .replace("{{header_image}}", generatePriceQuotationPDFDto.getOtherInformation().getHeader())
                .replace("{{customer_name}}", generatePriceQuotationPDFDto.getOtherInformation().getCustomerName())
                .replace("{{activity}}", generatePriceQuotationPDFDto.getOtherInformation().getActivity().equals("LINK_BUILDING") ? "link building" : "digital PR")
                .replace("{{signature}}", generatePriceQuotationPDFDto.getOtherInformation().getSignature())
                .replace("{{total_without_tax}}", totalExpense.toString())
                .replace("{{tax}}", taxExpense.toString())
                .replace("{{total}}", Double.toString(total))
                .replace("{{rows}}", tableBody);


        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            HtmlConverter.convertToPdf(fileContent, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting file", e);
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Error exporting file");
        }

    }
}
