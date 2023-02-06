package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.content.FindContentFilterDTO;
import it.xtreamdev.gflbe.dto.content.PublishOnWordpressDTO;
import it.xtreamdev.gflbe.dto.content.SaveContentDTO;
import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import it.xtreamdev.gflbe.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/content")
@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping
    private Page<Content> findAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC", required = false) String sortDirection,
            FindContentFilterDTO findContentFilterDTO
    ) {
        return this.contentService.findAll(findContentFilterDTO,PageRequest
                .of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy));
    }

    @GetMapping("{id}")
    private Content findById(@PathVariable Integer id) {
        return this.contentService.findById(id);
    }

    @PutMapping("{id}")
    private void update(@PathVariable Integer id, @RequestBody SaveContentDTO saveContentDTO) {
        this.contentService.update(id, saveContentDTO);
    }

    @PutMapping("{id}/status/{status}")
    private void changeStatus(@PathVariable Integer id, @PathVariable String status) {
        this.contentService.changeStatus(id, ContentStatus.valueOf(status));
    }

    @PutMapping("{id}/assign/{editorId}")
    private void assign(@PathVariable Integer id, @PathVariable Integer editorId) {
        this.contentService.assignToEditor(id, editorId);
    }

    @GetMapping("{id}/export")
    private byte[] exportDocx(@PathVariable Integer id){
        return this.contentService.exportDocx(id);
    }

    @PostMapping("{id}/publishOnWordpress")
    private void publishOnWordpress(@PathVariable Integer id, @RequestBody PublishOnWordpressDTO publishOnWordpressDTO){
        this.contentService.publishOnWordpress(id,publishOnWordpressDTO);
    }
}
