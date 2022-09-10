package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.FindOrderDTO;
import it.xtreamdev.gflbe.dto.GenerateOrderFromOrderPackDTO;
import it.xtreamdev.gflbe.dto.SaveDraftOrderDTO;
import it.xtreamdev.gflbe.dto.SaveOrderDTO;
import it.xtreamdev.gflbe.model.*;
import it.xtreamdev.gflbe.model.enumerations.OrderStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.NewspaperRepository;
import it.xtreamdev.gflbe.repository.OrderElementRepository;
import it.xtreamdev.gflbe.repository.OrderPackRepository;
import it.xtreamdev.gflbe.repository.OrderRepository;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
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
}
