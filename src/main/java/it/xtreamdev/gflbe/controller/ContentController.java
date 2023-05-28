package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.chatgpt.ChatGPTRequest;
import it.xtreamdev.gflbe.dto.chatgpt.ChatGPTResponse;
import it.xtreamdev.gflbe.dto.common.PaginationRequest;
import it.xtreamdev.gflbe.dto.content.*;
import it.xtreamdev.gflbe.dto.content.wordpress.categories.WordpressCategoryResponse;
import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.model.enumerations.ContentStatus;
import it.xtreamdev.gflbe.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/content")
@RestController
public class ContentController {

    @Autowired
    public ContentService contentService;

    @GetMapping
    public Page<Content> findAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "DESC", required = false) String sortDirection,
            FindContentFilterDTO findContentFilterDTO
    ) {
        return this.contentService.findAll(findContentFilterDTO, PageRequest
                .of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy.split(",")));
    }

    @GetMapping("titles")
    public Page<TitleResponseDTO> getTitles(SearchTitleRequestDTO searchTitleRequestDTO, PaginationRequest paginationRequest) {
        return this.contentService.searchTitle(searchTitleRequestDTO, paginationRequest.getPageRequest());
    }

    @GetMapping("{id}")
    public Content findById(@PathVariable Integer id) {
        return this.contentService.findById(id);
    }

    @PutMapping("{id}")
    public void update(@PathVariable Integer id, @RequestBody SaveContentDTO saveContentDTO) {
        this.contentService.update(id, saveContentDTO);
    }

    @PutMapping("{id}/status/{status}")
    public void changeStatus(@PathVariable Integer id, @PathVariable String status) {
        this.contentService.changeStatus(id, ContentStatus.valueOf(status));
    }

    @PutMapping("{id}/assign/{editorId}")
    public void assign(@PathVariable Integer id, @PathVariable Integer editorId) {
        this.contentService.assignToEditor(id, editorId);
    }

    @GetMapping("{id}/export")
    public byte[] exportDocx(@PathVariable Integer id) {
        return this.contentService.exportDocx(id);
    }

    @PostMapping("{id}/publishOnWordpress")
    public void publishOnWordpress(@PathVariable Integer id, @RequestBody PublishOnWordpressDTO publishOnWordpressDTO) {
        this.contentService.publishOnWordpress(id, publishOnWordpressDTO);
    }

    @GetMapping("{id}/wordpressCategory")
    public List<WordpressCategoryResponse> getDomainCategory(@PathVariable Integer id) {
        return this.contentService.getCategoriesWordpress(id);
    }

    @PostMapping("assistant")
    public ChatGPTResponse getDomainCategory(@RequestBody ChatGPTRequest chatGPTRequest) {
        return this.contentService.doChatGPTRequest(chatGPTRequest);
    }



}
