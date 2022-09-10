package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.FindOrderPackDTO;
import it.xtreamdev.gflbe.dto.SaveOrderPackDTO;
import it.xtreamdev.gflbe.model.OrderPack;
import it.xtreamdev.gflbe.service.OrderPackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/order-pack")
public class OrderPackController {

    @Autowired
    private OrderPackService orderPackService;

    @GetMapping
    private ResponseEntity<Page<OrderPack>> find(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection,
            FindOrderPackDTO findOrderPackDTO
    ) {
        return ResponseEntity.ok(this.orderPackService.find(findOrderPackDTO, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy)));
    }

    @GetMapping("{id}")
    private ResponseEntity<OrderPack> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(this.orderPackService.findById(id));
    }


    @PostMapping
    private ResponseEntity<OrderPack> save(
            @RequestBody SaveOrderPackDTO saveOrderPackDTO
    ) {
        return ResponseEntity.ok().body(this.orderPackService.save(saveOrderPackDTO));
    }

    @PutMapping("{id}")
    private ResponseEntity<OrderPack> update(
            @PathVariable Integer id,
            @RequestBody SaveOrderPackDTO saveOrderPackDTO
    ) {
        return ResponseEntity.ok().body(this.orderPackService.update(id, saveOrderPackDTO));
    }

    @DeleteMapping("{id}")
    private ResponseEntity<Void> delete(
            @PathVariable Integer id
    ) {
        this.orderPackService.delete(id);
        return ResponseEntity.ok().build();
    }

}
