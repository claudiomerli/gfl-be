package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.common.PaginationRequest;
import it.xtreamdev.gflbe.dto.genericorder.SaveSecondLevelOrderDTO;
import it.xtreamdev.gflbe.dto.genericorder.SaveVideoOrderDTO;
import it.xtreamdev.gflbe.dto.genericorder.SearchGenericOrderDTO;
import it.xtreamdev.gflbe.model.GenericOrder;
import it.xtreamdev.gflbe.model.SecondLevelOrder;
import it.xtreamdev.gflbe.model.VideoOrder;
import it.xtreamdev.gflbe.service.GenericOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/generic-order")
public class GenericOrderController {

    @Autowired
    private GenericOrderService genericOrderService;

    @PostMapping("second-level")
    public SecondLevelOrder placeSecondLevelOrder(@RequestBody SaveSecondLevelOrderDTO secondLevelOrderDTO) {
        return this.genericOrderService.saveSecondLevelOrder(secondLevelOrderDTO);
    }

    @PostMapping("video")
    public VideoOrder placeVideoOrder(@RequestBody SaveVideoOrderDTO saveVideoOrderDTO) {
        return this.genericOrderService.saveVideoOrder(saveVideoOrderDTO);
    }

    @GetMapping("{id}")
    public GenericOrder findById(@PathVariable Integer id) {
        return this.genericOrderService.findById(id);
    }

    @PutMapping("{id}/confirm")
    public void confirm(@PathVariable Integer id) {
        this.genericOrderService.confirm(id);
    }

    @PutMapping("{id}/refuse")
    public void refuse(@PathVariable Integer id) {
        this.genericOrderService.refuse(id);
    }

    @GetMapping
    public Page<GenericOrder> search(
            PaginationRequest paginationRequest,
            SearchGenericOrderDTO findSecondLevelDTO) {
        return this.genericOrderService.find(findSecondLevelDTO, paginationRequest.getPageRequest());
    }


}
