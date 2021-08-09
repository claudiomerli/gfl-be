package it.xtreamdev.gflbe.controller;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Deprecated
@Controller
public class ContentController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private NewspaperService newspaperService;

    @GetMapping("admin/contents")
    public String findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            @ModelAttribute("searchContentDto") SearchContentDTO searchContentDTO,
            Model model
    ) {
        model.addAttribute("customers", this.customerService.findAll());
        model.addAttribute("editors", this.userService.findEditors());
        model.addAttribute("newspapers", this.newspaperService.findAll());
        model.addAttribute("contents", this.contentService.findAll(searchContentDTO, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy)));
        return "views/admin/content-list";
    }

    @GetMapping("admin/contents/create")
    public String create(
            @RequestParam(value = "editorId", required = false) Integer editorId,
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "newspaperId", required = false) Integer newspaperId,
            @ModelAttribute("saveContentDto") SaveContentDTO saveContentDto,
            Model model
    ) {
        Page<User> allEditors = this.userService.findEditors(PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.fromString("ASC"), "fullname"));
        Page<Customer> allCustomers = this.customerService.findAll(PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.fromString("ASC"), "name"));
        List<Newspaper> newspapers = this.newspaperService.findAll();
        model.addAttribute("editors", allEditors);
        model.addAttribute("customers", allCustomers);
        model.addAttribute("newspapers", newspapers);

        if (Objects.nonNull(customerId)) {
            model.addAttribute("selectedCustomer", allCustomers.stream().filter(customer -> customer.getId().equals(customerId)).findFirst().orElse(null));
            saveContentDto.setCustomerId(customerId);
        }

        if (Objects.nonNull(editorId)) {
            saveContentDto.setEditorId(editorId);
        }

        if (Objects.nonNull(newspaperId)) {
            saveContentDto.setNewspaperId(newspaperId);
        }

        return "views/admin/content-create";
    }

    @PostMapping("admin/contents/create")
    public String create(
            @ModelAttribute("saveContentDto") SaveContentDTO saveContentDTO
    ) {
        this.contentService.save(saveContentDTO);
        return "redirect:/admin/contents/";
    }

    @GetMapping("admin/contents/{contentId}/edit")
    public String edit(
            @PathVariable Integer contentId,
            @RequestParam(value = "editorId", required = false) Integer editorId,
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "newspaperId", required = false) Integer newspaperId,
            Model model
    ) {
        Page<User> allEditors = this.userService.findEditors(PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.fromString("ASC"), "fullname"));
        Page<Customer> allCustomers = this.customerService.findAll(PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.fromString("ASC"), "name"));
        List<Newspaper> newspapers = this.newspaperService.findAll();
        model.addAttribute("editors", allEditors);
        model.addAttribute("customers", allCustomers);
        model.addAttribute("newspapers", newspapers);

        SaveContentDTO saveContentDto = this.contentService.loadSaveContentDto(contentId);
        model.addAttribute("selectedCustomer", saveContentDto.getContent().getCustomer());

        if (Objects.nonNull(editorId)) {
            saveContentDto.setEditorId(editorId);
        }

        if (Objects.nonNull(newspaperId)) {
            saveContentDto.setNewspaperId(newspaperId);
        }

        if (Objects.nonNull(customerId)) {
            model.addAttribute("selectedCustomer", allCustomers.stream().filter(customer -> customer.getId().equals(customerId)).findFirst().orElse(null));
            saveContentDto.setCustomerId(customerId);
        }


        model.addAttribute("saveContentDto", saveContentDto);
        return "views/admin/content-edit";
    }

    @PostMapping("admin/contents/{contentId}/edit")
    public String editContent(
            @PathVariable Integer contentId,
            @ModelAttribute("saveContentDto") SaveContentDTO saveContentDTO
    ) {
        this.contentService.update(contentId, saveContentDTO);
        return "redirect:/admin/contents/" + contentId + "/edit";
    }

    @GetMapping("admin/contents/{contentId}/delete")
    public String deleteContent(@PathVariable Integer contentId) {
        this.contentService.delete(contentId);
        return "redirect:/admin/contents";
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
    public String editorContents(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "globalSearch", required = false) String globalSearch,
            Model model
    ) {
        model.addAttribute("contents", this.contentService.loadCurrentEditorContents(PageRequest.of(page, Integer.MAX_VALUE, Sort.Direction.fromString(sortDirection), sortBy)));
        return "views/editor/contents-assigned";
    }

    @GetMapping("editor/contents/{contentId}/edit")
    public String editorEditContent(
            @PathVariable Integer contentId,
            @AuthenticationPrincipal UserPrincipal principal,
            Model model
    ) {
        model.addAttribute("content", this.contentService.loadContentByIdAndUser(contentId, principal));
        return "views/editor/content-edit";
    }

    @PostMapping("editor/contents/{contentId}/edit")
    public String editorEditContent(
            @PathVariable Integer contentId,
            @AuthenticationPrincipal UserPrincipal principal,
            @ModelAttribute("content") Content content
    ) {
        this.contentService.update(contentId, principal, content);
        return "redirect:/editor/contents/" + contentId + "/edit";
    }

    @GetMapping("customer/content/{contentId}")
    public String viewContentCustomer(
            @PathVariable Integer contentId,
            @RequestParam("token") String token,
            Model model
    ) {
        model.addAttribute("content", this.contentService.loadByIdAndToken(contentId, token));
        return "views/customer/content-view";
    }

    @GetMapping("customer/approve/{contentId}")
    public String approveContent(
            @PathVariable Integer contentId,
            @RequestParam("token") String token
    ) {
        this.contentService.approveContent(contentId, token);
        return "redirect:/customer/content/" + contentId + "?token=" + token;
    }

    @PostMapping("customer/notes/{contentId}")
    public String saveNotes(
            @PathVariable Integer contentId,
            @RequestParam("token") String token,
            @RequestParam("notes") String notes
    ) {
        this.contentService.saveNotesToContent(contentId, notes, token);
        return "redirect:/customer/content/" + contentId + "?token=" + token;
    }


}
