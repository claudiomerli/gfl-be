package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.genericorder.SaveSecondLevelOrderDTO;
import it.xtreamdev.gflbe.dto.genericorder.SaveVideoOrderDTO;
import it.xtreamdev.gflbe.dto.genericorder.SearchGenericOrderDTO;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.GenericOrderType;
import it.xtreamdev.gflbe.model.enumerations.GenericOrderLevel;
import it.xtreamdev.gflbe.model.enumerations.OrderStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GenericOrderService {

    @Autowired
    private GenericOrderRepository genericOrderRepository;

    @Autowired
    private VideoTemplateRepository videoTemplateRepository;

    @Autowired
    private UserService userService;

    public Page<GenericOrder> find(SearchGenericOrderDTO searchGenericOrderDTO, Pageable pageRequest) {
        User user = this.userService.userInfo();

        return this.genericOrderRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(searchGenericOrderDTO.getOrderType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), GenericOrderType.valueOf(searchGenericOrderDTO.getOrderType())));
            }

            if (StringUtils.isNotBlank(searchGenericOrderDTO.getOrderLevel())) {
                predicates.add(criteriaBuilder.equal(root.get("level"), GenericOrderLevel.valueOf(searchGenericOrderDTO.getOrderLevel())));
            }

            if (StringUtils.isNotBlank(searchGenericOrderDTO.getOrderStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), OrderStatus.valueOf(searchGenericOrderDTO.getOrderStatus())));
            }

            if (user.getRole() == RoleName.CUSTOMER) {
                predicates.add(criteriaBuilder.equal(root.get("customer"), user));
            } else if (searchGenericOrderDTO.getCustomerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), searchGenericOrderDTO.getCustomerId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);
    }

    public SecondLevelOrder saveSecondLevelOrder(SaveSecondLevelOrderDTO saveSecondLevelOrderDTO) {
        User user = this.userService.userInfo();

        if (user.getRole() != RoleName.CUSTOMER) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Only a customer can place a second level order");
        }

        SecondLevelOrder secondLevelOrder = SecondLevelOrder
                .builder()
                .type(GenericOrderType.SECOND_LEVEL)
                .status(OrderStatus.REQUESTED)
                .link(saveSecondLevelOrderDTO.getLink())
                .level(saveSecondLevelOrderDTO.getLevel())
                .customer(user)
                .build();

        return this.genericOrderRepository.save(secondLevelOrder);
    }

    public VideoOrder saveVideoOrder(SaveVideoOrderDTO saveVideoOrderDTO) {
        User user = this.userService.userInfo();

        if (user.getRole() != RoleName.CUSTOMER) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Only a customer can place a second level order");
        }

        VideoTemplate videoTemplate = this.videoTemplateRepository.findById(saveVideoOrderDTO.getTemplateId()).orElseThrow();
        if (!saveVideoOrderDTO.getFields().stream().allMatch(mapEntry ->
                videoTemplate.getFields().stream().anyMatch(videoTemplateField -> videoTemplateField.getField().equals(mapEntry.getName())))) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Some field are not present in template");
        }

        VideoOrder videoOrder = VideoOrder.builder()
                .videoTemplate(videoTemplate)
                .type(GenericOrderType.VIDEO)
                .customer(user)
                .level(GenericOrderLevel.NOT_SPECIFIED)
                .status(OrderStatus.REQUESTED)
                .build();

        videoOrder
                .getFields()
                .addAll(saveVideoOrderDTO
                        .getFields()
                        .stream()
                        .map(mapEntry ->
                                VideoOrderField
                                        .builder()
                                        .name(mapEntry.getName())
                                        .value(mapEntry.getValue())
                                        .order(videoOrder)
                                        .build()
                        ).collect(Collectors.toList()));

        return this.genericOrderRepository.save(videoOrder);
    }

    public void confirm(Integer id) {
        this.genericOrderRepository.confirmOrder(id);
    }

    public void refuse(Integer id) {
        this.genericOrderRepository.refuseOrder(id);
    }

    public GenericOrder findById(Integer id) {
        return this.genericOrderRepository.findById(id).orElseThrow();
    }
}

