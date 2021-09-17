package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.CustomerNotes;
import it.xtreamdev.gflbe.dto.ResponseContentDTO;
import it.xtreamdev.gflbe.dto.SaveContentDTO;
import it.xtreamdev.gflbe.dto.SearchContentDTO;
import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.security.model.JwtUserPrincipal;
import it.xtreamdev.gflbe.service.ContentService;
import it.xtreamdev.gflbe.service.CustomerService;
import it.xtreamdev.gflbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping
    public ResponseEntity<Page<Content>> search(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            SearchContentDTO searchContentDTO) {

        return ResponseEntity.ok(this.contentService.findAll(searchContentDTO, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy)));
    }

    @GetMapping("{contentId}")
    public ResponseEntity<Content> findById(@PathVariable Integer contentId) {
        return ResponseEntity.ok(this.contentService.findById(contentId));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody SaveContentDTO saveContentDTO) {
        this.contentService.save(saveContentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{contentId}")
    public ResponseEntity<Void> update(@PathVariable Integer contentId,
                                       @RequestBody SaveContentDTO saveContentDTO) {

        this.contentService.update(contentId, saveContentDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{contentId}")
    public ResponseEntity<Void> deleteContent(@PathVariable Integer contentId) {
        this.contentService.delete(contentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{contentId}/deliver")
    public ResponseEntity<Void> deliverContent(@PathVariable Integer contentId) {
        this.contentService.deliverContent(contentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{contentId}/exportDocx")
    public ResponseEntity<byte[]> exportDocx(
            @PathVariable Integer contentId
    ) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;filename=%s.docx", "export"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(this.contentService.exportDocx(contentId));
    }

    @GetMapping("{contentId}/exportPdf")
    public ResponseEntity<byte[]> exportPdf(
            @PathVariable Integer contentId
    ) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;filename=%s.pdf", "export"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(this.contentService.exportPdf(contentId));
    }


    @GetMapping("customer/{contentId}")
    public ResponseEntity<Content> viewContentCustomer(
            @PathVariable Integer contentId,
            @RequestParam("token") String token
    ) {
        return ResponseEntity.ok(
                this.contentService.loadByIdAndToken(contentId, token)
        );
    }

    @PutMapping("customer/{contentId}/approve")
    public ResponseEntity<Void> approveContent(
            @PathVariable Integer contentId,
            @RequestParam("token") String token
    ) {
        this.contentService.approveContent(contentId, token);
        return ResponseEntity.ok().build();
    }

    @PutMapping("customer/{contentId}/notes")
    public ResponseEntity<Void> saveNotes(
            @PathVariable Integer contentId,
            @RequestParam("token") String token,
            @RequestBody CustomerNotes customerNotes
    ) {
        this.contentService.saveNotesToContent(contentId, customerNotes.getNotes(), token);
        return ResponseEntity.ok().build();
    }


}
