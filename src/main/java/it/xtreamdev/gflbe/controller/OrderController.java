package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.FindOrderDTO;
import it.xtreamdev.gflbe.dto.SaveDraftOrderDTO;
import it.xtreamdev.gflbe.dto.SaveOrderDTO;
import it.xtreamdev.gflbe.model.Order;
import it.xtreamdev.gflbe.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    private ResponseEntity<Page<Order>> find(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection,
            FindOrderDTO findOrderDTO
    ) {
        return ResponseEntity.ok(this.orderService.find(findOrderDTO, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy)));
    }

    @GetMapping("{id}")
    private ResponseEntity<Order> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(this.orderService.findById(id));
    }

    @PostMapping("draft")
    private ResponseEntity<Order> saveDraft(
            @RequestBody SaveDraftOrderDTO saveDraftOrderDTO
            ) {
        return ResponseEntity.ok().body(this.orderService.saveDraft(saveDraftOrderDTO));
    }

    @PutMapping("{id}/send")
    private ResponseEntity<Order> send(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(this.orderService.send(id));
    }

    @PostMapping
    private ResponseEntity<Order> save(@RequestBody SaveOrderDTO saveOrderDTO) {
        return ResponseEntity.ok().body(this.orderService.save(saveOrderDTO));
    }

    @PutMapping("{id}")
    private ResponseEntity<Order> update(
            @PathVariable Integer id,
            @RequestBody SaveOrderDTO saveOrderDTO
    ) {
        return ResponseEntity.ok().body(this.orderService.update(id, saveOrderDTO));
    }

    @PutMapping("{id}/addOrderElement")
    private ResponseEntity<Order> addOrderToElement(
            @PathVariable Integer id,
            @RequestBody SaveOrderDTO.SaveOrderElementDTO saveOrderElementDTO
    ) {
        return ResponseEntity.ok().body(this.orderService.addOrderElement(id, saveOrderElementDTO));
    }

    @DeleteMapping("{id}")
    private ResponseEntity<Void> delete(
            @PathVariable Integer id
    ) {
        this.orderService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}/confirm")
    private ResponseEntity<Void> confirm(
            @PathVariable Integer id
    ) {
        this.orderService.confirm(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}/cancel")
    private ResponseEntity<Void> cancel(
            @PathVariable Integer id
    ) {
        this.orderService.cancel(id);
        return ResponseEntity.ok().build();
    }


}
