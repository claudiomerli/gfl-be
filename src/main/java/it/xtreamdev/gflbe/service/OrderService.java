package it.xtreamdev.gflbe.service;

import com.itextpdf.html2pdf.HtmlConverter;
import it.xtreamdev.gflbe.dto.*;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.OrderStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderPackRepository orderPackRepository;

    @Autowired
    private OrderElementRepository orderElementRepository;

    @Autowired
    private NewspaperRepository newspaperRepository;

    @Autowired
    private RequestQuoteRepository requestQuoteRepository;

    @Autowired
    private RequestQuotePriceReplacementRepository requestQuotePriceReplacementRepository;

    @Autowired
    private UserService userService;

    public Order findById(Integer id) {
        return this.orderRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "ID not found"));
    }

    public Order addOrderElement(Integer id, SaveOrderDTO.SaveOrderElementDTO orderElementDTO) {
        Order order = this.findById(id);
        order.getOrderElements().add(OrderElement.builder()
                .contentNumber(orderElementDTO.getContentNumber())
                .order(order)
                .newspaper(this.newspaperRepository.findById(orderElementDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id newspaper not found")))
                .build()
        );
        return this.orderRepository.save(order);
    }

    @Transactional
    public Order update(Integer id, SaveOrderDTO saveOrderDTO) {
        Order order = findById(id);
        order.setName(saveOrderDTO.getName());
        order.setNote(saveOrderDTO.getNote());

        this.orderElementRepository.deleteByOrder(order);
        order.setOrderElements(saveOrderDTO.getElements().stream().map(saveOrderElementDTO -> OrderElement.builder()
                        .contentNumber(saveOrderElementDTO.getContentNumber())
                        .newspaper(this.newspaperRepository.findById(saveOrderElementDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id newspaper not found")))
                        .order(order)
                        .build())
                .collect(Collectors.toList()));

        return this.orderRepository.save(order);
    }

    public Page<Order> find(FindOrderDTO findOrderDTO, PageRequest pageRequest) {
        User user = this.userService.userInfo();
        return this.orderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Order, OrderElement> orderElements = root.join("orderElements", JoinType.LEFT);
            Join<OrderElement, Newspaper> newspaper = orderElements.join("newspaper", JoinType.LEFT);
            criteriaQuery.distinct(true);

            if (user.getRole() == RoleName.CUSTOMER) {
                predicates.add(criteriaBuilder.equal(root.get("customer"), user));
            } else if (user.getRole() == RoleName.ADMIN && Objects.nonNull(findOrderDTO.getCustomerId())) {
                predicates.add(criteriaBuilder.equal(root.get("customer"), findOrderDTO.getCustomerId()));
            }

            if (user.getRole() == RoleName.ADMIN) {
                predicates.add(criteriaBuilder.notEqual(root.get("status"), OrderStatus.DRAFT));
            }

            if (StringUtils.isNotBlank(findOrderDTO.getName())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + findOrderDTO.getName().toUpperCase() + "%"));
            }

            if (StringUtils.isNotBlank(findOrderDTO.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), OrderStatus.valueOf(findOrderDTO.getStatus())));
            }

            if (StringUtils.isNotBlank(findOrderDTO.getExcludeOrderPack()) && Boolean.parseBoolean(findOrderDTO.getExcludeOrderPack())) {
                predicates.add(criteriaBuilder.isNull(root.get("orderPack")));
            }

            if (!findOrderDTO.getNewspaperIds().isEmpty()) {
                CriteriaBuilder.In<Integer> orderElementIdInExpression = criteriaBuilder.in(newspaper.get("id"));
                findOrderDTO.getNewspaperIds().forEach(orderElementIdInExpression::value);
                predicates.add(orderElementIdInExpression);
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);
    }

    @Transactional
    public void delete(Integer id) {
        this.orderRepository.deleteById(id);
    }

    public void confirm(Integer id) {
        Order order = findById(id);
        order.setStatus(OrderStatus.CONFIRMED);
        this.orderRepository.save(order);
    }

    public void cancel(Integer id) {
        Order order = findById(id);
        order.setStatus(OrderStatus.CANCELED);
        this.orderRepository.save(order);
    }


    public Order saveDraft(SaveDraftOrderDTO saveDraftOrderDTO) {
        User user = this.userService.userInfo();
        return this.orderRepository.save(Order
                .builder()
                .status(OrderStatus.DRAFT)
                .name(saveDraftOrderDTO.getName())
                .customer(user)
                .build()
        );
    }

    public Order send(Integer id) {
        Order order = this.findById(id);
        order.setStatus(OrderStatus.REQUESTED);

        return this.orderRepository.save(order);
    }

    public Order generate(GenerateOrderFromOrderPackDTO generateOrderFromOrderPackDTO) {
        OrderPack orderPack = this.orderPackRepository.findById(generateOrderFromOrderPackDTO.getIdOrderPack()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "ID not found"));
        User user = this.userService.userInfo();

        Order orderToSave = Order.builder()
                .orderPack(orderPack)
                .orderPackPrice(orderPack.getPrice())
                .name(generateOrderFromOrderPackDTO.getName())
                .customer(user)
                .status(OrderStatus.DRAFT)
                .build();

        orderToSave.setOrderElements(
                orderPack.getOrderElements().stream().map(orderElementOrderPack ->
                        OrderElement.builder()
                                .order(orderToSave)
                                .newspaper(orderElementOrderPack.getNewspaper())
                                .contentNumber(orderElementOrderPack.getContentNumber())
                                .build()
                ).collect(Collectors.toList())
        );

        return this.orderRepository.save(orderToSave);
    }

    @Transactional
    public byte[] generateRequestQuote(GenerateRequestQuoteDTO generateRequestQuoteDTO, String format) {
        Resource resource = new ClassPathResource("pdf-generation/request-quote.html");
        Order order = this.findById(generateRequestQuoteDTO.getOrderId());
        try (InputStream inputStream = resource.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            fileContent = fileContent
                    .replace("$$HEADER$$", generateRequestQuoteDTO.getHeader())
                    .replace("$$ORDER_NAME$$", order.getName())
                    .replace("$$NOTE$$", order.getNote())
                    .replace("$$SIGNATURE$$", generateRequestQuoteDTO.getSignature())
                    .replace("$$TOTAL$$", NumberFormat.getCurrencyInstance(Locale.ITALY).format(generateRequestQuoteDTO.getPriceReplacements().stream().map(GenerateRequestQuoteDTO.PriceReplacementDTO::getPriceReplacement).reduce(Double::sum).orElse(0.0)));

            fileContent = substituteImageWithTrailingTag(fileContent);
            fileContent = substituteRows(generateRequestQuoteDTO, order, fileContent);

            if (format.equals("pdf")) {
                HtmlConverter.convertToPdf(fileContent, baos);
            } else if (format.equals("docx")) {
                WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
                XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
                wordMLPackage.getMainDocumentPart().getContent().addAll(XHTMLImporter.convert(substituteDocxSpecialCharacters(fileContent), null));
                wordMLPackage.save(baos);
            }

            if (Objects.nonNull(generateRequestQuoteDTO.getRequestQuoteId())) {
                updateRequestQuote(generateRequestQuoteDTO, generateRequestQuoteDTO.getRequestQuoteId());
            } else {
                persistRequestQuote(generateRequestQuoteDTO, order);
            }


            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Errore durante la generazione", e);
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void updateRequestQuote(GenerateRequestQuoteDTO generateRequestQuoteDTO, Integer requestQuoteId) {
        RequestQuote requestQuote = this.requestQuoteRepository.findById(requestQuoteId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id not found"));
        requestQuote.setHeader(generateRequestQuoteDTO.getHeader());
        requestQuote.setSignature(generateRequestQuoteDTO.getSignature());

        this.requestQuotePriceReplacementRepository.deleteByRequestQuote(requestQuote);

        requestQuote.setRequestQuotePriceReplacements(
                generateRequestQuoteDTO.getPriceReplacements().stream().map(priceReplacementDTO -> RequestQuotePriceReplacement.builder()
                        .requestQuote(requestQuote)
                        .priceReplacement(priceReplacementDTO.getPriceReplacement())
                        .newspaper(this.newspaperRepository.findById(priceReplacementDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "NewspaperId not found")))
                        .build()).collect(Collectors.toList()));

        this.requestQuoteRepository.save(requestQuote);
    }

    private void persistRequestQuote(GenerateRequestQuoteDTO generateRequestQuoteDTO, Order order) {
        RequestQuote requestQuote = RequestQuote
                .builder()
                .order(order)
                .header(generateRequestQuoteDTO.getHeader())
                .signature(generateRequestQuoteDTO.getSignature())
                .build();

        requestQuote.setRequestQuotePriceReplacements(
                generateRequestQuoteDTO.getPriceReplacements().stream().map(priceReplacementDTO -> RequestQuotePriceReplacement.builder()
                        .requestQuote(requestQuote)
                        .priceReplacement(priceReplacementDTO.getPriceReplacement())
                        .newspaper(this.newspaperRepository.findById(priceReplacementDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "NewspaperId not found")))
                        .build()).collect(Collectors.toList())
        );

        this.requestQuoteRepository.save(requestQuote);
    }

    public List<RequestQuote> findRequestQuoteByOrderId(Integer id) {
        return this.requestQuoteRepository.findByOrder_Id(id);
    }

    private static String substituteDocxSpecialCharacters(String fileContent) {
        return fileContent
                .replace("&nbsp;", " ")
                .replace("&ograve;", "ò")
                .replace("&agrave;", "à")
                .replace("&eacute;", "é")
                .replace("&egrave;", "è")
                .replace("&ugrave;", "ù")
                .replace("&igrave;", "ì")
                .replace("&Egrave;", "È")
                .replace("&Eacute;", "É")
                .replace("&rsquo;", "’")
                .replace("&lsquo;", "‘");
    }

    private static String substituteRows(GenerateRequestQuoteDTO generateRequestQuoteDTO, Order order, String fileContent) {
        StringBuilder sbRows = new StringBuilder();
        order.getOrderElements().forEach(orderElement -> {
            sbRows.append("<tr><td>")
                    .append(orderElement.getNewspaper().getName())
                    .append("</td><td>")
                    .append(orderElement.getContentNumber())
                    .append("</td><td style=\"text-align:right\">")
                    .append(NumberFormat.getCurrencyInstance(Locale.ITALY).format(generateRequestQuoteDTO.getPriceReplacements().stream()
                            .filter(priceReplacementDTO -> priceReplacementDTO.getNewspaperId().equals(orderElement.getNewspaper().getId()))
                            .findFirst().orElseThrow(() -> new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during generation"))
                            .getPriceReplacement()))
                    .append("</td></tr>");
        });

        fileContent = fileContent.replace("$$ROWS$$", sbRows.toString());
        return fileContent;
    }

    private static String substituteImageWithTrailingTag(String fileContent) {
        Pattern pattern = Pattern.compile("<img.*?>");
        Matcher matcher = pattern.matcher(fileContent);
        StringBuilder sbImages = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sbImages, matcher.group() + "</img>");
        }
        matcher.appendTail(sbImages);
        fileContent = sbImages.toString();
        return fileContent;
    }

    public void deleteRequestQuote(Integer id) {
        this.requestQuoteRepository.deleteById(id);
    }
}
