package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.ResponseContentDTO;
import it.xtreamdev.gflbe.dto.SaveContentDTO;
import it.xtreamdev.gflbe.dto.SearchContentDTO;
import it.xtreamdev.gflbe.model.Content;
import it.xtreamdev.gflbe.model.Customer;
import it.xtreamdev.gflbe.model.Newspaper;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.security.UserPrincipal;
import it.xtreamdev.gflbe.service.ContentService;
import it.xtreamdev.gflbe.service.CustomerService;
import it.xtreamdev.gflbe.service.NewspaperService;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
public class ContentRestController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private NewspaperService newspaperService;

    @GetMapping("admin/contents")
    public ResponseEntity<ResponseContentDTO> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            @ModelAttribute("searchContentDto") SearchContentDTO searchContentDTO) {

        ResponseContentDTO contents = ResponseContentDTO.builder()
                .customers(this.customerService.findAll())
                .editors(this.userService.findEditors())
                .newspapers(this.newspaperService.findAll())
                .contents(this.contentService.findAll(searchContentDTO, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy)))
                .build();

        return ResponseEntity.ok(contents);
    }

    @PostMapping("admin/contents/create")
    public ResponseEntity<Void> create(@RequestBody SaveContentDTO saveContentDTO) {
        this.contentService.save(saveContentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("admin/contents/{contentId}/edit")
    public ResponseEntity<Void> editContent(@PathVariable Integer contentId,
                                            @RequestBody SaveContentDTO saveContentDTO) {

        this.contentService.update(contentId, saveContentDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/contents/{contentId}/delete")
    public ResponseEntity<Void> deleteContent(@PathVariable Integer contentId) {
        this.contentService.delete(contentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("admin/contents/{contentId}/exportDocx")
    public ResponseEntity<byte[]> exportDocx(
            @PathVariable Integer contentId
    ) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;filename=%s.docx", "export"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(this.contentService.exportDocx(contentId));
    }

    @GetMapping("admin/contents/{contentId}/exportPdf")
    public ResponseEntity<byte[]> exportPdf(
            @PathVariable Integer contentId
    ) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;filename=%s.pdf", "export"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(this.contentService.exportPdf(contentId));
    }

    @GetMapping("editor/contents")
    public ResponseEntity<Page<Content>> editorContents(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "globalSearch", required = false) String globalSearch
    ) {

        return ResponseEntity.ok(
                this.contentService.loadCurrentEditorContents(PageRequest.of(page, Integer.MAX_VALUE, Sort.Direction.fromString(sortDirection), sortBy))
        );
    }

    @GetMapping("editor/contents/{contentId}/edit")
    public ResponseEntity<Content> editorEditContent(
            @PathVariable Integer contentId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                this.contentService.loadContentByIdAndUser(contentId, principal)
        );
    }

    @PutMapping("editor/contents/{contentId}/edit")
    public ResponseEntity<Void> editorEditContent(
            @PathVariable Integer contentId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Content content
    ) {
        this.contentService.update(contentId, principal, content);
        return ResponseEntity.ok().build();
    }

    @GetMapping("customer/content/{contentId}")
    public ResponseEntity<Content> viewContentCustomer(
            @PathVariable Integer contentId,
            @RequestParam("token") String token
    ) {
        return ResponseEntity.ok(
                this.contentService.loadByIdAndToken(contentId, token)
        );
    }

    @PostMapping("customer/approve/{contentId}")
    public ResponseEntity<Void> approveContent(
            @PathVariable Integer contentId,
            @RequestParam("token") String token
    ) {
        this.contentService.approveContent(contentId, token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("customer/notes/{contentId}")
    public ResponseEntity<Void> saveNotes(
            @PathVariable Integer contentId,
            @RequestParam("token") String token,
            @RequestParam("notes") String notes
    ) {
        this.contentService.saveNotesToContent(contentId, notes, token);
        return ResponseEntity.ok().build();
    }


}
