package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.FindOrderPackDTO;
import it.xtreamdev.gflbe.dto.SaveOrderPackDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.model.Order;
import it.xtreamdev.gflbe.model.OrderElement;
import it.xtreamdev.gflbe.model.OrderPack;
import it.xtreamdev.gflbe.repository.NewspaperRepository;
import it.xtreamdev.gflbe.repository.OrderElementRepository;
import it.xtreamdev.gflbe.repository.OrderPackRepository;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderPackService {

    @Autowired
    private OrderPackRepository orderPackRepository;

    @Autowired
    private OrderElementRepository orderElementRepository;

    @Autowired
    private NewspaperRepository newspaperRepository;

    public OrderPack findById(Integer id) {
        return this.orderPackRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "ID not found"));
    }

    @Transactional
    public OrderPack save(SaveOrderPackDTO saveOrderPackDTO) {
        OrderPack orderPack = OrderPack.builder()
                .name(saveOrderPackDTO.getName())
                .description(saveOrderPackDTO.getDescription())
                .price(saveOrderPackDTO.getPrice())
                .build();

        orderPack.setOrderElements(
                saveOrderPackDTO.getElements().stream().map(saveOrderElementDTO -> OrderElement.builder()
                        .contentNumber(saveOrderElementDTO.getContentNumber())
                        .newspaper(this.newspaperRepository.findById(saveOrderElementDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id newspaper not found")))
                        .orderPack(orderPack)
                        .build()).collect(Collectors.toList())
        );

        return this.orderPackRepository.save(orderPack);
    }

    @Transactional
    public OrderPack update(Integer id, SaveOrderPackDTO saveOrderPackDTO) {
        OrderPack orderPack = findById(id);
        orderPack.setName(saveOrderPackDTO.getName());
        orderPack.setDescription(saveOrderPackDTO.getDescription());
        orderPack.setPrice(saveOrderPackDTO.getPrice());

        this.orderElementRepository.deleteByOrderPack(orderPack);
        orderPack.setOrderElements(saveOrderPackDTO.getElements().stream().map(saveOrderElementDTO -> OrderElement.builder()
                        .contentNumber(saveOrderElementDTO.getContentNumber())
                        .newspaper(this.newspaperRepository.findById(saveOrderElementDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id newspaper not found")))
                        .orderPack(orderPack)
                        .build())
                .collect(Collectors.toList()));

        return this.orderPackRepository.save(orderPack);
    }

    @Transactional
    public void delete(Integer id) {
        OrderPack orderPack = this.findById(id);
        orderPack.setDeleted(true);
        this.orderPackRepository.save(orderPack);
    }

    public Page<OrderPack> find(FindOrderPackDTO findOrderPackDTO, PageRequest pageRequest) {
        return this.orderPackRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Order, OrderElement> orderElements = root.join("orderElements", JoinType.LEFT);
            Join<OrderElement, Newspaper> newspaper = orderElements.join("newspaper", JoinType.LEFT);
            criteriaQuery.distinct(true);
            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

            if (StringUtils.isNotBlank(findOrderPackDTO.getGlobalSearch())) {
                predicates.addAll(
                        Arrays.stream(findOrderPackDTO.getGlobalSearch().split(" "))
                                .map(portion ->
                                    criteriaBuilder.or(
                                          criteriaBuilder.like(criteriaBuilder.upper(root.get("name")),"%" + portion.toUpperCase() + "%"),
                                          criteriaBuilder.like(criteriaBuilder.upper(root.get("description")),"%" + portion.toUpperCase() + "%")
                                    )
                                ).collect(Collectors.toList())
                );
            }

            if (!findOrderPackDTO.getNewspaperIds().isEmpty()) {
                CriteriaBuilder.In<Integer> orderElementIdInExpression = criteriaBuilder.in(newspaper.get("id"));
                findOrderPackDTO.getNewspaperIds().forEach(orderElementIdInExpression::value);
                predicates.add(orderElementIdInExpression);
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageRequest);
    }

}
