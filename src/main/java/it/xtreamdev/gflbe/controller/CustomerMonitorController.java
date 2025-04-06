package it.xtreamdev.gflbe.controller;


import it.xtreamdev.gflbe.dto.project.CustomerMonitorListDTO;
import it.xtreamdev.gflbe.dto.project.SaveCustomerMonitorDTO;
import it.xtreamdev.gflbe.model.CustomerMonitor;
import it.xtreamdev.gflbe.service.CustomerMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/customer-monitor")
@RestController
public class CustomerMonitorController {

    @Autowired
    private CustomerMonitorService customerMonitorService;

    @GetMapping
    public ResponseEntity<Page<CustomerMonitorListDTO>> getCustomerMonitorList(
            @RequestParam Integer customerId, Pageable pageable) {
        return ResponseEntity.ok(customerMonitorService.findCustomerMonitorList(customerId, pageable));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<CustomerMonitor>> getCustomerMonitors(@PathVariable Integer customerId) {
        return ResponseEntity.ok(customerMonitorService.getCustomerMonitors(customerId));
    }

    @PostMapping
    public ResponseEntity<CustomerMonitor> saveCustomerMonitor(@RequestBody SaveCustomerMonitorDTO saveCustomerMonitorDTO) {
        return ResponseEntity.ok(customerMonitorService.save(saveCustomerMonitorDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerMonitor(@PathVariable Integer id) {
        customerMonitorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
