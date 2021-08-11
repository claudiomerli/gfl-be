package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.dto.SaveProjectDTO;
import it.xtreamdev.gflbe.dto.SearchProjectDTO;
import it.xtreamdev.gflbe.dto.UpdateProjectDTO;
import it.xtreamdev.gflbe.exception.GLFException;
import it.xtreamdev.gflbe.model.Customer;
import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.repository.CustomerRepository;
import it.xtreamdev.gflbe.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final CustomerRepository customerRepository;

    public Page<Project> search(SearchProjectDTO searchProjectDTO, PageRequest pageRequest) {
        return this.projectRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(searchProjectDTO.getGlobalSearch())
                    .ifPresent(globalSearchValue ->
                            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + globalSearchValue.toUpperCase() + "%"))
                    );
            Optional.ofNullable(searchProjectDTO.getCustomerId())
                    .ifPresent(customerIdValue -> {
                        Join<Project, Customer> customerJoin = root.join("customer");
                        predicates.add(criteriaBuilder.equal(customerJoin.get("id"), customerIdValue));
                    });

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }, pageRequest);
    }

    public Project save(SaveProjectDTO saveProjectDTO) {
        return projectRepository.save(
                Project.builder()
                        .name(saveProjectDTO.getName())
                        .customer(
                                customerRepository.findById(saveProjectDTO.getCustomerId())
                                        .orElseThrow(() -> new GLFException("customer not found", HttpStatus.UNPROCESSABLE_ENTITY))
                        )
                        .build()
        );
    }

    public Project update(Integer id, UpdateProjectDTO updateProjectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new GLFException("project not found", HttpStatus.NOT_FOUND));
        project.setName(updateProjectDTO.getName());
        project.setCustomer(
                customerRepository.findById(updateProjectDTO.getCustomerId())
                        .orElseThrow(() -> new GLFException("customer not found", HttpStatus.UNPROCESSABLE_ENTITY))
        );
        return projectRepository.save(project);
    }
}
