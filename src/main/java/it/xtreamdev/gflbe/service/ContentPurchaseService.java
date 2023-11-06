package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.purchasecontent.SaveContentPurchaseDTO;
import it.xtreamdev.gflbe.dto.purchasecontent.FindContentPurchaseDTO;
import it.xtreamdev.gflbe.model.ContentPurchase;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.repository.ContentPurchaseRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Predicate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ContentPurchaseService {

    @Autowired
    private ContentPurchaseRepository contentPurchaseRepository;

    @Autowired
    private NewspaperService newspaperService;

    public byte[] exportContentPurchase(FindContentPurchaseDTO findContentPurchaseDTO, PageRequest pageRequest) {
        Page<ContentPurchase> contentPurchase = this.findContentPurchase(findContentPurchaseDTO, pageRequest);
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Acquisti contenuti");
            Row headerRow = sheet.createRow(0);
            List<String> headerCells = List.of("Importo", "Numero redazionali acquistati", "Costo cadauno", "Numero redazionali rimanenti", "Testate", "Testate", "Scadenza");
            IntStream.range(0, headerCells.size())
                    .forEachOrdered(value -> {
                        Cell cell = headerRow.createCell(value);
                        cell.setCellValue(headerCells.get(value));
                    });

            IntStream.range(1, contentPurchase.getContent().size()).forEachOrdered(value -> {
                Row row = sheet.createRow(value);
                ContentPurchase contentPurchaseElement = contentPurchase.getContent().get(value);
                Cell cellUrl = row.createCell(0);
                cellUrl.setCellValue(contentPurchaseElement.getAmount() != null ? contentPurchaseElement.getAmount() : 0);

                Cell cellPublicationUrl = row.createCell(1);
                cellPublicationUrl.setCellValue(contentPurchaseElement.getContentNumber() != null ? contentPurchaseElement.getContentNumber() : 0);

                Cell cellAnchor = row.createCell(2);
                cellAnchor.setCellValue(contentPurchaseElement.getEachCost() != null ? contentPurchaseElement.getEachCost() : 0);

                Cell cellIsOnline = row.createCell(3);
                cellIsOnline.setCellValue(contentPurchaseElement.getContentRemaining() != null ? contentPurchaseElement.getContentRemaining() : 0);

                Cell cellIsIndex = row.createCell(4);
                cellIsIndex.setCellValue(contentPurchaseElement.getNewspapers() != null ? contentPurchaseElement.getNewspapers().stream().map(Newspaper::getName).collect(Collectors.joining(", ")) : "");

            });

            IntStream.range(0, 5).forEach(sheet::autoSizeColumn);
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Page<ContentPurchase> findContentPurchase(FindContentPurchaseDTO findContentPurchaseDTO, PageRequest pageRequest) {
        return this.contentPurchaseRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(findContentPurchaseDTO.getGlobalSearch())) {
                Arrays.asList(findContentPurchaseDTO.getGlobalSearch().split(" ")).forEach(searchPortion ->
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("note")), "%" + searchPortion.toUpperCase() + "%"))
                );
            }

            if (findContentPurchaseDTO.getNewspaperId() != null) {
                Newspaper newspaper = this.newspaperService.findById(findContentPurchaseDTO.getNewspaperId());
                predicates.add(criteriaBuilder.isMember(newspaper, root.get("newspapers")));
            }

            if (findContentPurchaseDTO.getExpirationFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expiration"), findContentPurchaseDTO.getExpirationFrom()));
            }

            if (findContentPurchaseDTO.getExpirationTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expiration"), findContentPurchaseDTO.getExpirationTo()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }), pageRequest);
    }

    public ContentPurchase findById(Integer id) {
        return this.contentPurchaseRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
    }

    public ContentPurchase save(SaveContentPurchaseDTO saveContentPurchaseDTO) {
        return this.contentPurchaseRepository.save(ContentPurchase
                .builder()
                .contentNumber(saveContentPurchaseDTO.getContentNumber())
                .note(saveContentPurchaseDTO.getNote())
                .amount(saveContentPurchaseDTO.getAmount())
                .newspapers(saveContentPurchaseDTO.getNewspapers().stream().map(integer -> this.newspaperService.findById(integer)).collect(Collectors.toList()))
                .expiration(saveContentPurchaseDTO.getExpiration())
                .build());
    }

    public ContentPurchase update(Integer id, SaveContentPurchaseDTO saveContentPurchaseDTO) {
        ContentPurchase contentPurchaseToUpdate = this.findById(id);

        contentPurchaseToUpdate.getNewspapers().clear();

        contentPurchaseToUpdate.setContentNumber(saveContentPurchaseDTO.getContentNumber());
        contentPurchaseToUpdate.setAmount(saveContentPurchaseDTO.getAmount());
        contentPurchaseToUpdate.getNewspapers().addAll(saveContentPurchaseDTO.getNewspapers().stream().map(integer -> this.newspaperService.findById(integer)).collect(Collectors.toList()));
        contentPurchaseToUpdate.setNote(saveContentPurchaseDTO.getNote());
        contentPurchaseToUpdate.setExpiration(saveContentPurchaseDTO.getExpiration());

        return this.contentPurchaseRepository.save(contentPurchaseToUpdate);
    }

    public void delete(Integer id) {
        this.contentPurchaseRepository.deleteById(id);
    }


}
