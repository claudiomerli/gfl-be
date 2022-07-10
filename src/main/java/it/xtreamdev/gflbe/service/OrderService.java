package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.FindOrderDTO;
import it.xtreamdev.gflbe.dto.SaveOrderDTO;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.model.Order;
import it.xtreamdev.gflbe.model.OrderElement;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.model.enumerations.OrderStatus;
import it.xtreamdev.gflbe.model.enumerations.RoleName;
import it.xtreamdev.gflbe.repository.NewspaperRepository;
import it.xtreamdev.gflbe.repository.OrderElementRepository;
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
    private OrderElementRepository orderElementRepository;

    @Autowired
    private NewspaperRepository newspaperRepository;

    @Autowired
    private UserService userService;

    public Order findById(Integer id) {
        return this.orderRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "ID not found"));
    }

    @Transactional
    public Order save(SaveOrderDTO saveOrderDTO) {
        User user = this.userService.userInfo();

        Order order = this.orderRepository.save(Order.builder().status(OrderStatus.REQUESTED).customer(user).build());
        order.setNote(saveOrderDTO.getNote());
        order.setOrderElements(saveOrderDTO.getElements().stream().map(saveOrderElementDTO -> OrderElement.builder()
                .contentNumber(saveOrderElementDTO.getContentNumber())
                .newspaper(this.newspaperRepository.findById(saveOrderElementDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id newspaper not found")))
                .order(order)
                .build()).collect(Collectors.toList()));
        return this.orderRepository.save(order);
    }

    @Transactional
    public Order update(Integer id, SaveOrderDTO saveOrderDTO) {
        Order order = findById(id);
        order.setNote(saveOrderDTO.getNote());

//        this.orderElementRepository.deleteByOrder(order);
//        order.setOrderElements(saveOrderDTO.getElements().stream().map(saveOrderElementDTO -> OrderElement.builder()
//                .contentNumber(saveOrderElementDTO.getContentNumber())
//                .newspaper(this.newspaperRepository.findById(saveOrderElementDTO.getNewspaperId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Id newspaper not found")))
//                .order(order)
//                .build()).collect(Collectors.toList()));

        return this.orderRepository.save(order);
    }

    public Page<Order> find(FindOrderDTO findOrderDTO, PageRequest pageRequest) {
        User user = this.userService.userInfo();
        return this.orderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Order, OrderElement> orderElements = root.join("orderElements");
            Join<OrderElement, Newspaper> newspaper = orderElements.join("newspaper");
            criteriaQuery.distinct(true);

            if (user.getRole() == RoleName.CUSTOMER) {
                predicates.add(criteriaBuilder.equal(root.get("customer"), user));
            } else if (user.getRole() == RoleName.ADMIN && Objects.nonNull(findOrderDTO.getCustomerId())) {
                predicates.add(criteriaBuilder.equal(root.get("customer"), findOrderDTO.getCustomerId()));
            }

            if (StringUtils.isNotBlank(findOrderDTO.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), OrderStatus.valueOf(findOrderDTO.getStatus())));
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


}
