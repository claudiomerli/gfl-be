package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.ChangeStatusProjectDTO;
import it.xtreamdev.gflbe.dto.SaveProjectDTO;
import it.xtreamdev.gflbe.dto.SearchProjectDTO;
import it.xtreamdev.gflbe.dto.UpdateProjectDTO;
import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.repository.ProjectRepository;
import it.xtreamdev.gflbe.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/project")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity<Page<Project>> search(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            @ModelAttribute("searchProjectDTO") SearchProjectDTO searchProjectDTO) {
        return ResponseEntity.ok(
                projectService.search(searchProjectDTO, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy))
        );
    }

    @PostMapping
    public ResponseEntity<Project> save(@RequestBody SaveProjectDTO saveProjectDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                projectService.save(saveProjectDTO)
        );
    }

    @PutMapping("{id}")
    public ResponseEntity<Project> update(@PathVariable Integer id, @RequestBody UpdateProjectDTO updateProjectDTO) {
        return ResponseEntity.ok(
                projectService.update(id, updateProjectDTO)
        );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        projectRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}/change-status")
    public ResponseEntity<Project> changeStatus(@PathVariable Integer id, @Valid @RequestBody ChangeStatusProjectDTO changeStatusProjectDTO) {
        return ResponseEntity.ok(
                projectService.changeStatus(id, changeStatusProjectDTO.getStatus())
        );
    }
}
