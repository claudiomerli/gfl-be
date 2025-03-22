package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.newspaper.*;
import it.xtreamdev.gflbe.dto.topic.TopicDTO;
import it.xtreamdev.gflbe.mapper.NewspaperMapper;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.NewspaperDiscountRepository;
import it.xtreamdev.gflbe.repository.NewspaperRepository;
import it.xtreamdev.gflbe.repository.ProjectRepository;
import it.xtreamdev.gflbe.repository.TopicRepository;
import it.xtreamdev.gflbe.util.FormatUtils;
import it.xtreamdev.gflbe.util.PdfUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static it.xtreamdev.gflbe.util.ExcelUtils.*;

@Service
@Slf4j
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
    private TopicRepository topicRepository;
    @Autowired
    private NewspaperDiscountRepository newspaperDiscountRepository;

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
            Optional.ofNullable(searchNewspaperDTO.getNofollow()).ifPresent(nofollow -> predicates.add(criteriaBuilder.equal(root.get("nofollow"), nofollow)));

            Optional.ofNullable(searchNewspaperDTO.getZaFrom()).ifPresent(zaFrom -> predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("za"), zaFrom)));
            Optional.ofNullable(searchNewspaperDTO.getZaTo()).ifPresent(zaTo -> predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("za"), zaTo)));
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
                List<Integer> idToExclude = project.getProjectCommissions().stream().map(ProjectCommission::getNewspaper).filter(Objects::nonNull).map(Newspaper::getId).collect(Collectors.toList());
                predicates.add(root.get("id").in(idToExclude).not());
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);


        return newspaperMapper.mapEntityToDTO(newspapers);
    }

    public Page<NewspaperDTO> findForCustomer(SearchNewspaperCustomerDTO searchNewspaperCustomerDTO, PageRequest pageRequest) {
        Page<Newspaper> newspapers = this.newspaperRepository
                .findAll((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    predicates.add(criteriaBuilder.isFalse(root.get("hidden")));

                    if (StringUtils.isNotBlank(searchNewspaperCustomerDTO.getGlobalSearch())) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + searchNewspaperCustomerDTO.getGlobalSearch().toUpperCase() + "%"));
                    }

                    if (Objects.nonNull(searchNewspaperCustomerDTO.getProjectId())) {
                        Project project = this.projectRepository.findById(searchNewspaperCustomerDTO.getProjectId()).orElseThrow();
                        List<Integer> idToExclude = project.getProjectCommissions().stream().map(ProjectCommission::getNewspaper).filter(Objects::nonNull).map(Newspaper::getId).collect(Collectors.toList());
                        if (!idToExclude.isEmpty()) {
                            predicates.add(root.get("id").in(idToExclude).not());
                        }
                    }

                    if (Objects.nonNull(searchNewspaperCustomerDTO.getTopicId())) {
                        predicates.add(criteriaBuilder.isMember(searchNewspaperCustomerDTO.getTopicId(), root.get("topics")));
                    }

                    if (Objects.nonNull(searchNewspaperCustomerDTO.getMaxCost())) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("costSell"), searchNewspaperCustomerDTO.getMaxCost()));
                    }

                    if (StringUtils.isNotBlank(searchNewspaperCustomerDTO.getTypology())) {
                        predicates.add(criteriaBuilder.equal(root.get("regionalGeolocalization"), searchNewspaperCustomerDTO.getTypology()));
                    }

                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }, pageRequest);

        return newspaperMapper.mapEntityToDTO(newspapers);
    }

    public NewspaperCustomerFilterPopulationDTO getNewspaperPopulationFilter() {
        User currentUser = userService.userInfo();
        List<Project> customerProjects = this.projectRepository.findByCustomer(currentUser);
        Double maxPriceForCustomer = this.newspaperRepository.findMaxPriceForCustomer();
        Double minPriceForCustomer = this.newspaperRepository.findMinPriceForCustomer();
        List<Topic> topics = this.topicRepository.findAll();

        return NewspaperCustomerFilterPopulationDTO
                .builder()
                .projects(customerProjects.stream().map(Project::toLazyProject).collect(Collectors.toList()))
                .maxNewspaperCost(maxPriceForCustomer)
                .minNewspaperCost(minPriceForCustomer)
                .topics(topics.stream().map(Topic::toDto).collect(Collectors.toList()))
                .build();
    }

    public NewspaperDTO save(SaveNewspaperDTO newspaper) {
        return newspaperMapper.mapEntityToDTO(this.newspaperRepository.save(
                Newspaper
                        .builder()
                        .name(newspaper.getName())
                        .costEach(newspaper.getCostEach())
                        .costSell(newspaper.getCostSell())
                        .email(newspaper.getEmail())
                        .regionalGeolocalization(newspaper.getRegionalGeolocalization())
                        .note(newspaper.getNote())
                        .za(newspaper.getZa())
                        .tf(newspaper.getTf())
                        .cf(newspaper.getCf())
                        .dr(newspaper.getDr())
                        .traffic(newspaper.getTraffic())
                        .ip(newspaper.getIp())
                        .hidden(newspaper.getHidden())
                        .sensitiveTopics(newspaper.getSensitiveTopics())
                        .nofollow(newspaper.getNofollow())
                        .warning(newspaper.getWarning())
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
        persistedNewspaper.setRegionalGeolocalization(saveNewspaperDTO.getRegionalGeolocalization());
        persistedNewspaper.setNote(saveNewspaperDTO.getNote());
        persistedNewspaper.setTopics(saveNewspaperDTO.getTopics().stream().map(topicId -> Topic.builder().id(topicId).build()).collect(Collectors.toSet()));
        persistedNewspaper.setZa(saveNewspaperDTO.getZa());
        persistedNewspaper.setTf(saveNewspaperDTO.getTf());
        persistedNewspaper.setCf(saveNewspaperDTO.getCf());
        persistedNewspaper.setDr(saveNewspaperDTO.getDr());
        persistedNewspaper.setTraffic(saveNewspaperDTO.getTraffic());
        persistedNewspaper.setIp(saveNewspaperDTO.getIp());
        persistedNewspaper.setTopics(saveNewspaperDTO.getTopics().stream().map(topicId -> Topic.builder().id(topicId).build()).collect(Collectors.toSet()));
        persistedNewspaper.setHidden(saveNewspaperDTO.getHidden());
        persistedNewspaper.setSensitiveTopics(saveNewspaperDTO.getSensitiveTopics());
        persistedNewspaper.setWarning(saveNewspaperDTO.getWarning());
        persistedNewspaper.setNofollow(saveNewspaperDTO.getNofollow());

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
        return FinanceDTO.builder()
                .purchasesValue(this.newspaperRepository.totalCost())
                .salesValue(this.newspaperRepository.totalSell())
                .amountRemaining(this.newspaperRepository.totalRemaining())
                .build();
    }

    public byte[] exportExcel(SearchNewspaperDTO searchNewspaperDTO, PageRequest pageRequest) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            List<NewspaperDTO> listaDTO = listForExport(searchNewspaperDTO, pageRequest);
            XSSFWorkbook spreadsheet = createSpreadsheet();
            XSSFSheet exportTestate = addSheet(spreadsheet, "Export testate");
            addRow(exportTestate, "ID", "Nome", "Redazionali acquistati", "Redazionali rimanenti", "Costo cadauno", "Costo di vendita", "ZA", "TF", "CF", "DR", "Traffico", "E-mail di contatto", "Geolocalizzazione regionale", "Argomento");

            listaDTO.forEach(dto -> addRow(exportTestate,
                    dto.getId(),
                    dto.getName(),
                    dto.getPurchasedContent(),
                    dto.getLeftContent(),
                    dto.getCostEach(),
                    dto.getCostSell(),
                    dto.getZa(),
                    dto.getTf(),
                    dto.getCf(),
                    dto.getDr(),
                    dto.getTraffic(),
                    dto.getEmail(),
                    dto.getRegionalGeolocalization(),
                    dto.getTopics().stream().map(TopicDTO::getName).collect(Collectors.joining(", "))
            ));

            spreadsheet.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error");
        }

    }

    @Transactional
    public void importExcel(byte[] bytes) {
        try (ByteArrayInputStream file = new ByteArrayInputStream(bytes)) {
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if(row.getRowNum() == 0) {continue;}
                double idValue = row.getCell(0).getNumericCellValue();
                double zaValue = row.getCell(6).getNumericCellValue();
                double tfValue = row.getCell(7).getNumericCellValue();
                double cfValue = row.getCell(8).getNumericCellValue();
                double drValue = row.getCell(9).getNumericCellValue();
                double trafficValue = row.getCell(10).getNumericCellValue();

                this.newspaperRepository.updateNewspaperIndexes((int) zaValue, (int) tfValue, (int) cfValue, (int) drValue, (int) trafficValue, (int) idValue);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public byte[] exportPDF(SearchNewspaperDTO searchNewspaperDTO, PageRequest pageRequest) throws IOException {
        List<NewspaperDTO> listaDTO = listForExport(searchNewspaperDTO, pageRequest);
        pdfUtils.exportPdf("Elenco testate censite", Arrays.asList("ID", "Nome", "Redazionali rimanenti", "Costo cadauno", "Costo di vendita", "ZA", "TF", "CF", "DR", "Traffico", "E-mail di contatto", "Geolocalizzazione regionale", "Argomento"));
        listaDTO.forEach(dto -> {
            pdfUtils.setValore(dto.getId());
            pdfUtils.setValore(dto.getName());
            pdfUtils.setValore(dto.getLeftContent());
            pdfUtils.setValore(dto.getCostEach());
            pdfUtils.setValore(dto.getCostSell());
            pdfUtils.setValore(dto.getZa());
            pdfUtils.setValore(dto.getTf());
            pdfUtils.setValore(dto.getCf());
            pdfUtils.setValore(dto.getDr());
            pdfUtils.setValore(dto.getTraffic() != null ? new FormatUtils.HighNumberValue(dto.getTraffic()).shortString() : null);
            pdfUtils.setValore(dto.getEmail());
            pdfUtils.setValore(dto.getRegionalGeolocalization());
            pdfUtils.setValore(dto.getTopics().stream().map(TopicDTO::getName).collect(Collectors.joining(", ")));
        });
        return pdfUtils.completaPDF();
    }

    private List<NewspaperDTO> listForExport(SearchNewspaperDTO searchNewspaperDTO, PageRequest pageRequest) {
        return this.findAll(searchNewspaperDTO, pageRequest).toList();
    }

    public byte[] generateReport(List<GenerateNewspaperReportDTO> generateNewspaperReportRequest) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XSSFWorkbook spreadsheet = createSpreadsheet();
            XSSFSheet report = addSheet(spreadsheet, "Resoconto testate");
            addRow(report, "Identificativo Testata", "Nome", "Costo di acquisto", "Costo di vendita originale", "Costo di vendita");

            List<Newspaper> newspapers = new ArrayList<>();
            generateNewspaperReportRequest.forEach(generateNewspaperReportDTO -> {
                Newspaper newspaper = this.findById(generateNewspaperReportDTO.getId());
                addRow(report,
                        newspaper.getId(),
                        newspaper.getName(),
                        newspaper.getCostEach(),
                        newspaper.getCostSell(),
                        generateNewspaperReportDTO.getCostSell()
                );
                newspapers.add(newspaper);
            });

            double totalCostEach = newspapers.stream().mapToDouble(Newspaper::getCostEach).sum();
            double totalCostSell = generateNewspaperReportRequest.stream().mapToDouble(GenerateNewspaperReportDTO::getCostSell).sum();

            addEmptyRow(report);
            addRow(report, "Totale Costo di acquisto", totalCostEach);
            addRow(report, "Totale Costo di vendita", totalCostSell);
            addRow(report, "Differenza", totalCostSell - totalCostEach);

            spreadsheet.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error");
        }
    }

    public byte[] generateReportCustomer(List<GenerateNewspaperCustomerReportDTO> generateNewspaperCustomerReportDTOS) {
        User user = userService.userInfo();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XSSFWorkbook spreadsheet = createSpreadsheet();
            XSSFSheet report = addSheet(spreadsheet, "Resoconto testate");
            addRow(report, "Nome", "Costo di vendita");

            List<Newspaper> newspapers = new ArrayList<>();
            List<Double> prices = new ArrayList<>();
            generateNewspaperCustomerReportDTOS.forEach(generateNewspaperReportDTO -> {
                Newspaper newspaper = this.findById(generateNewspaperReportDTO.getId());
                Double finalCostSell = this.newspaperDiscountRepository
                        .findByCustomerAndNewspaper(user, newspaper)
                        .map(newspaperDiscount -> newspaper.getCostSell() - ((Double.valueOf(newspaperDiscount.getDiscountPercentage()) / 100) * newspaper.getCostSell())).orElse(newspaper.getCostSell());
                prices.add(finalCostSell);
                addRow(report,
                        newspaper.getName(),
                        finalCostSell
                );
                newspapers.add(newspaper);
            });

            double totalCostSell = prices.stream().mapToDouble(value -> value).sum();

            addEmptyRow(report);
            addRow(report, "Totale Costo di vendita", totalCostSell);

            spreadsheet.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error");
        }
    }

    public void saveDescription(Integer id, SaveNewspaperDescriptionDTO saveNewspaperDescriptionDTO) {
        Newspaper newspaper = this.findById(id);
        newspaper.setDescription(saveNewspaperDescriptionDTO.getDescription());
        this.newspaperRepository.save(newspaper);
    }

    public NewspaperDescriptionDTO getDescription(Integer id) {
        Newspaper newspaper = this.findById(id);
        return NewspaperDescriptionDTO.builder().description(newspaper.getDescription()).build();
    }
}
