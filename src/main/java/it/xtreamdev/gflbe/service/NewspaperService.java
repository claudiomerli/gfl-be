package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.newspaper.FinanceDTO;
import it.xtreamdev.gflbe.dto.newspaper.MaxMinRangeNewspaperAttributesDTO;
import it.xtreamdev.gflbe.dto.newspaper.NewspaperDTO;
import it.xtreamdev.gflbe.dto.newspaper.SaveNewspaperDTO;
import it.xtreamdev.gflbe.dto.newspaper.SearchNewspaperDTO;
import it.xtreamdev.gflbe.dto.topic.TopicDTO;
import it.xtreamdev.gflbe.mapper.NewspaperMapper;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.NewspaperRepository;
import it.xtreamdev.gflbe.repository.ProjectRepository;
import it.xtreamdev.gflbe.util.PdfUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.xlsx4j.sml.SheetData;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static it.xtreamdev.gflbe.util.ExcelUtils.*;

@Service
public class NewspaperService {

    @Autowired
    private UserService userService;
    @Autowired
    private NewspaperRepository newspaperRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private NewspaperMapper newspaperMapper;

    @Autowired
    private PdfUtils pdfUtils;

    public MaxMinRangeNewspaperAttributesDTO maxMinRangeNewspaperAttributes() {
        return this.newspaperRepository.getMaxMinRangeNewspaperAttributes();
    }

    public Page<NewspaperDTO> findAll(SearchNewspaperDTO searchNewspaperDTO, PageRequest pageRequest) {
        User currentUser = userService.userInfo();
        Page<Newspaper> newspapers = this.newspaperRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            criteriaQuery.distinct(true);
            Join<Newspaper, Topic> topics = root.join("topics", JoinType.LEFT);

            if (currentUser.getRole().equals(RoleName.CUSTOMER)) {
                predicates.add(criteriaBuilder.isFalse(root.get("hidden")));
            } else {
                Optional.ofNullable(searchNewspaperDTO.getHidden()).ifPresent(hidden -> predicates.add(criteriaBuilder.equal(root.get("hidden"), hidden)));
            }

            Optional.ofNullable(searchNewspaperDTO.getSensitiveTopics()).ifPresent(sensitiveTopics -> predicates.add(criteriaBuilder.equal(root.get("sensitiveTopics"), sensitiveTopics)));

            Optional.ofNullable(searchNewspaperDTO.getZaFrom()).ifPresent(zaFrom -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("za"), zaFrom)));
            Optional.ofNullable(searchNewspaperDTO.getZaTo()).ifPresent(zaTo -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("za"), zaTo)));
            Optional.ofNullable(searchNewspaperDTO.getPurchasedContentFrom()).ifPresent(purchasedContentFrom -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("purchasedContent"), purchasedContentFrom)));
            Optional.ofNullable(searchNewspaperDTO.getPurchasedContentTo()).ifPresent(purchasedContentTo -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("purchasedContent"), purchasedContentTo)));
            Optional.ofNullable(searchNewspaperDTO.getLeftContentFrom()).ifPresent(leftContentFrom -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("leftContent"), leftContentFrom)));
            Optional.ofNullable(searchNewspaperDTO.getLeftContentTo()).ifPresent(leftContentTo -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("leftContent"), leftContentTo)));
            Optional.ofNullable(searchNewspaperDTO.getCostEachFrom()).ifPresent(costEachFrom -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("costEach"), costEachFrom)));
            Optional.ofNullable(searchNewspaperDTO.getCostEachTo()).ifPresent(costEachTo -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("costEach"), costEachTo)));
            Optional.ofNullable(searchNewspaperDTO.getCostSellFrom()).ifPresent(costSellFrom -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("costSell"), costSellFrom)));
            Optional.ofNullable(searchNewspaperDTO.getCostSellTo()).ifPresent(costSellTo -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("costSell"), costSellTo)));

            if (StringUtils.isNotBlank(searchNewspaperDTO.getId())) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchNewspaperDTO.getId()));
            }

            if (StringUtils.isNotBlank(searchNewspaperDTO.getName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + searchNewspaperDTO.getName().toUpperCase() + "%"));
            }

            if (Objects.nonNull(searchNewspaperDTO.getRegionalGeolocalization()) && !searchNewspaperDTO.getRegionalGeolocalization().isEmpty()) {
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get("regionalGeolocalization"));
                searchNewspaperDTO.getRegionalGeolocalization().forEach(inClause::value);
                predicates.add(inClause);
            }

            if (Objects.nonNull(searchNewspaperDTO.getTopics()) && !searchNewspaperDTO.getTopics().isEmpty()) {
                CriteriaBuilder.In<Integer> inClause = criteriaBuilder.in(topics.get("id"));
                searchNewspaperDTO.getTopics().forEach(inClause::value);
                predicates.add(inClause);
            }

            if (Objects.nonNull(searchNewspaperDTO.getNotUsedInProject())) {
                Project project = this.projectRepository.findById(searchNewspaperDTO.getNotUsedInProject()).orElseThrow();
                List<Integer> idToExclude = project.getProjectCommissions().stream().map(ProjectCommission::getNewspaper).map(Newspaper::getId).collect(Collectors.toList());
                predicates.add(root.get("id").in(idToExclude).not());
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);


        return newspaperMapper.mapEntityToDTO(newspapers);
    }

    public NewspaperDTO save(SaveNewspaperDTO newspaper) {
        return newspaperMapper.mapEntityToDTO(this.newspaperRepository.save(
                Newspaper
                        .builder()
                        .name(newspaper.getName())
                        .costEach(newspaper.getCostEach())
                        .costSell(newspaper.getCostSell())
                        .email(newspaper.getEmail())
                        .purchasedContent(newspaper.getPurchasedContent())
                        .regionalGeolocalization(newspaper.getRegionalGeolocalization())
                        .note(newspaper.getNote())
                        .za(newspaper.getZa())
                        .ip(newspaper.getIp())
                        .hidden(newspaper.getHidden())
                        .sensitiveTopics(newspaper.getSensitiveTopics())
                        .topics(newspaper.getTopics().stream().map(topicId -> Topic.builder().id(topicId).build()).collect(Collectors.toSet()))
                        .build()
        ));
    }

    public void delete(Integer id) {
        Newspaper newspaper = this.findById(id);
        newspaper.setHidden(true);
        this.newspaperRepository.save(newspaper);
    }

    public NewspaperDTO update(Integer id, SaveNewspaperDTO saveNewspaperDTO) {
        Newspaper persistedNewspaper = this.findById(id);

        persistedNewspaper.setName(saveNewspaperDTO.getName());
        persistedNewspaper.setEmail(saveNewspaperDTO.getEmail());
        persistedNewspaper.setCostEach(saveNewspaperDTO.getCostEach());
        persistedNewspaper.setCostSell(saveNewspaperDTO.getCostSell());
        persistedNewspaper.setPurchasedContent(saveNewspaperDTO.getPurchasedContent());
        persistedNewspaper.setRegionalGeolocalization(saveNewspaperDTO.getRegionalGeolocalization());
        persistedNewspaper.setNote(saveNewspaperDTO.getNote());
        persistedNewspaper.setTopics(saveNewspaperDTO.getTopics().stream().map(topicId -> Topic.builder().id(topicId).build()).collect(Collectors.toSet()));
        persistedNewspaper.setZa(saveNewspaperDTO.getZa());
        persistedNewspaper.setIp(saveNewspaperDTO.getIp());
        persistedNewspaper.setTopics(saveNewspaperDTO.getTopics().stream().map(topicId -> Topic.builder().id(topicId).build()).collect(Collectors.toSet()));
        persistedNewspaper.setHidden(saveNewspaperDTO.getHidden());
        persistedNewspaper.setSensitiveTopics(saveNewspaperDTO.getSensitiveTopics());

        return newspaperMapper.mapEntityToDTO(this.newspaperRepository.save(persistedNewspaper));
    }

    public NewspaperDTO detail(Integer id) {
        Newspaper newspaper = this.newspaperRepository
                .findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Newspaper not found"));
        return newspaperMapper.mapEntityToDTO(newspaper);
    }

    public Newspaper findById(Integer id) {
        return this.newspaperRepository
                .findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Newspaper not found"));
    }

    public FinanceDTO finance() {
        return this.newspaperRepository.finance();
    }

    public byte[] exportExcel(SearchNewspaperDTO searchNewspaperDTO, PageRequest pageRequest) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            List<NewspaperDTO> listaDTO = listForExport(searchNewspaperDTO, pageRequest);
            SpreadsheetMLPackage spreadsheet = createSpreadsheet();
            SheetData exportTestate = addSheet(spreadsheet, "Export testate");
            addRow(exportTestate, "ID", "Nome", "Redazionali acquistati", "Redazionali rimanenti", "Costo cadauno", "Costo di vendita", "ZA", "E-mail di contatto", "Geolocalizzazione regionale", "Argomento");

            listaDTO.forEach(dto -> addRow(exportTestate,
                    dto.getId(),
                    dto.getName(),
                    dto.getPurchasedContent(),
                    dto.getLeftContent(),
                    dto.getCostEach(),
                    dto.getCostSell(),
                    dto.getZa(),
                    dto.getEmail(),
                    dto.getRegionalGeolocalization(),
                    dto.getTopics().stream().map(TopicDTO::getName).collect(Collectors.joining(", "))
            ));
            spreadsheet.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error");
        }

    }

    public byte[] exportPDF(SearchNewspaperDTO searchNewspaperDTO, PageRequest pageRequest) throws IOException {
        List<NewspaperDTO> listaDTO = listForExport(searchNewspaperDTO, pageRequest);
        pdfUtils.exportPdf("Elenco testate censite", Arrays.asList("ID", "Nome", "Redazionali acquistati", "Redazionali rimanenti", "Costo cadauno", "Costo di vendita", "ZA", "E-mail di contatto", "Geolocalizzazione regionale", "Argomento"));
        listaDTO.forEach(dto -> {
            pdfUtils.setValore(dto.getId());
            pdfUtils.setValore(dto.getName());
            pdfUtils.setValore(dto.getPurchasedContent());
            pdfUtils.setValore(dto.getLeftContent());
            pdfUtils.setValore(dto.getCostEach());
            pdfUtils.setValore(dto.getCostSell());
            pdfUtils.setValore(dto.getZa());
            pdfUtils.setValore(dto.getEmail());
            pdfUtils.setValore(dto.getRegionalGeolocalization());
            pdfUtils.setValore(dto.getTopics().stream().map(TopicDTO::getName).collect(Collectors.joining(", ")));
        });
        return pdfUtils.completaPDF();
    }

    private List<NewspaperDTO> listForExport(SearchNewspaperDTO searchNewspaperDTO, PageRequest pageRequest) {
        return this.findAll(searchNewspaperDTO, pageRequest).toList();
    }

}
