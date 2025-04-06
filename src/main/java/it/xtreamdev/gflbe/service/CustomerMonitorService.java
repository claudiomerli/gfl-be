package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.project.CustomerMonitorListDTO;
import it.xtreamdev.gflbe.dto.project.SaveCustomerMonitorDTO;
import it.xtreamdev.gflbe.model.CustomerMonitor;
import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.repository.CustomerMonitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerMonitorService {

    @Autowired
    private CustomerMonitorRepository customerMonitorRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    public Page<CustomerMonitorListDTO> findCustomerMonitorList(Integer customerId, Pageable pageable) {
        return customerMonitorRepository.findCustomerMonitorList(customerId, pageable);
    }

    public List<CustomerMonitor> getCustomerMonitors(Integer customerId) {
        User customer = this.userService.findById(customerId);
        return this.customerMonitorRepository.findCustomerMonitorByCustomer(customer);
    }

    public CustomerMonitor save(SaveCustomerMonitorDTO saveCustomerMonitorDTO) {
        CustomerMonitor customerMonitor = Optional
                .ofNullable(saveCustomerMonitorDTO.getId())
                .map(integer -> this.customerMonitorRepository.getReferenceById(integer))
                .orElse(new CustomerMonitor());

        User customer = userService.findById(saveCustomerMonitorDTO.getCustomerId());
        Project project = projectService.findById(saveCustomerMonitorDTO.getProjectId());

        customerMonitor.setCustomer(customer);
        customerMonitor.setProject(project);
        customerMonitor.setStatus(saveCustomerMonitorDTO.getStatus());
        customerMonitor.setLastWork(saveCustomerMonitorDTO.getLastWork());
        customerMonitor.setCurrentlyMonthStatus(saveCustomerMonitorDTO.getCurrentlyMonthStatus());

        return this.customerMonitorRepository.save(customerMonitor);
    }

    public void delete(Integer id) {
        this.customerMonitorRepository.deleteById(id);
    }

}
