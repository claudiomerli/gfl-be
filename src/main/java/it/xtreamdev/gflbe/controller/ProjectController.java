package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.content.SaveAttachmentDTO;
import it.xtreamdev.gflbe.dto.content.SaveProjectCommissionHintDTO;
import it.xtreamdev.gflbe.dto.project.ProjectListElementDTO;
import it.xtreamdev.gflbe.dto.project.SaveProjectCommissionDTO;
import it.xtreamdev.gflbe.dto.project.SaveProjectDTO;
import it.xtreamdev.gflbe.dto.project.UpdateBulkProjectCommissionStatus;
import it.xtreamdev.gflbe.model.Project;
import it.xtreamdev.gflbe.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public Page<ProjectListElementDTO> find(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "globalSearch") String globalSearch,
            @RequestParam(value = "status") String status
    ) {
        return this.projectService.find(globalSearch, status,
                PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy)
        );
    }

    @GetMapping("{id}")
    public Project findById(@PathVariable Integer id) {
        return this.projectService.findById(id);
    }

    @PostMapping
    public Project save(@RequestBody SaveProjectDTO saveProjectDTO) {
        return this.projectService.save(saveProjectDTO);
    }

    @PutMapping("{id}")
    public Project save(@PathVariable Integer id, @RequestBody SaveProjectDTO saveProjectDTO) {
        return this.projectService.update(id, saveProjectDTO);
    }

    @PostMapping("{id}/commission")
    public Project saveCommission(
            @PathVariable Integer id,
            @RequestBody SaveProjectCommissionDTO saveProjectCommissionDTO
    ) {
        return this.projectService.addCommission(id, saveProjectCommissionDTO);
    }

    @DeleteMapping("{id}/commission/{idCommission}")
    public Project removeCommission(
            @PathVariable Integer id,
            @PathVariable Integer idCommission
    ) {
        return this.projectService.removeCommission(id, idCommission);
    }

    @PutMapping("{id}/commission/{idCommission}")
    public Project updateCommission(
            @PathVariable Integer id,
            @PathVariable Integer idCommission,
            @RequestBody SaveProjectCommissionDTO saveProjectCommissionDTO
    ) {
        return this.projectService.updateCommission(id, idCommission, saveProjectCommissionDTO);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        this.projectService.delete(id);
    }

    @PutMapping("{id}/commission/{idCommission}/{status}")
    public Project setStatusCommission(
            @PathVariable Integer id,
            @PathVariable Integer idCommission,
            @PathVariable String status
    ) {
        return this.projectService.setStatusCommission(id, idCommission, status);
    }

    @PutMapping("{id}/commission/bulk/{status}")
    public void setStatusCommission(
            @PathVariable Integer id,
            @PathVariable String status,
            @RequestBody UpdateBulkProjectCommissionStatus updateBulkProjectCommissionStatus
    ) {
        this.projectService.setBulkStatusCommission(id, status, updateBulkProjectCommissionStatus);
    }

    @PutMapping("{id}/invoice")
    public Project close(@PathVariable Integer id) {
        return this.projectService.invoiceProject(id);
    }

    @GetMapping("{id}/export")
    public byte[] exportExcel(@PathVariable Integer id) {
        return this.projectService.exportProjectExcel(id);
    }


    @PostMapping("{id}/commission/{idCommission}/hint/attachment")
    public void addProjectCommissionHintAttachment(
            @PathVariable Integer id,
            @PathVariable Integer idCommission,
            @RequestBody SaveAttachmentDTO saveAttachmentDTO
    ) {
        this.projectService.addProjectCommissionHintAttachment(id, idCommission, saveAttachmentDTO);
    }

    @DeleteMapping("{id}/commission/{idCommission}/hint/attachment/{idAttachment}")
    public void deleteProjectCommissionHintAttachment(
            @PathVariable Integer id,
            @PathVariable Integer idCommission,
            @PathVariable Integer idAttachment
    ) {
        this.projectService.deleteProjectCommissionHintAttachment(id, idCommission, idAttachment);
    }

    @PostMapping("{id}/hint/attachment")
    public void addProjectHintAttachment(
            @PathVariable Integer id,
            @RequestBody SaveAttachmentDTO saveAttachmentDTO
    ) {
        this.projectService.addProjectHintAttachment(id, saveAttachmentDTO);
    }

    @DeleteMapping("{id}/hint/attachment/{idAttachment}")
    public void deleteProjectHintAttachment(
            @PathVariable Integer id,
            @PathVariable Integer idAttachment
    ) {
        this.projectService.deleteProjectHintAttachment(id, idAttachment);
    }

    @PutMapping("{id}/hint")
    public void updateProjectHint(
            @PathVariable Integer id,
            @RequestBody SaveProjectCommissionHintDTO saveProjectCommissionHintDTO
    ) {
        this.projectService.updateProjectHint(id, saveProjectCommissionHintDTO);
    }

    @PutMapping("{id}/commission/{idCommission}/hint")
    public void updateProjectCommissionHint(
            @PathVariable Integer id,
            @PathVariable Integer idCommission,
            @RequestBody SaveProjectCommissionHintDTO saveProjectCommissionHintDTO
    ) {
        this.projectService.updateProjectCommissionHint(id, idCommission, saveProjectCommissionHintDTO);
    }
}
