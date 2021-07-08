package it.xtreamdev.gestioneattivita.controller;

import it.xtreamdev.gestioneattivita.dto.AdminStatusDTO;
import it.xtreamdev.gestioneattivita.dto.SaveCustomerDTO;
import it.xtreamdev.gestioneattivita.model.Newspaper;
import it.xtreamdev.gestioneattivita.model.User;
import it.xtreamdev.gestioneattivita.service.ContentService;
import it.xtreamdev.gestioneattivita.service.CustomerService;
import it.xtreamdev.gestioneattivita.service.NewspaperService;
import it.xtreamdev.gestioneattivita.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private NewspaperService newspaperService;

    @GetMapping("dashboard")
    public String dahsboard(Model model) {
        model.addAttribute("objectCounts",
                AdminStatusDTO.builder()
                        .contentCount(this.contentService.count())
                        .customersCount(this.customerService.count())
                        .editorCount(this.userService.countEditors())
                        .build()
        );

        return "views/admin/admin-dashboard";
    }

    @GetMapping("editors")
    public String findEditors(
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            Model model
    ) {
        model.addAttribute("editors",
                this.userService
                        .findEditors(
                                PageRequest.of(0, Integer.MAX_VALUE, Direction.fromString(sortDirection), sortBy.split(",")))
        );
        return "views/admin/editor-list";
    }

    @GetMapping("editors/create")
    public String createEditor(Model model) {
        model.addAttribute("editor", User.builder().build());
        return "views/admin/editor-create";
    }

    @PostMapping("editors/create")
    public String createEditor(@ModelAttribute("editor") @Valid User editor, BindingResult bindingResult) {
        this.userService.validateCreateEditor(editor, bindingResult);
        if (bindingResult.hasErrors()) {
            return "views/admin/editor-create";
        }

        this.userService.createEditor(editor);
        return "redirect:/admin/editors";
    }

    @GetMapping("editors/{id}/delete")
    public String deleteEditor(@PathVariable Integer id) {
        this.userService.delete(id);
        return "redirect:/admin/editors";
    }

    @GetMapping("editors/{id}/edit")
    public String editEditor(@PathVariable Integer id, Model model) {
        model.addAttribute("editor", this.userService.findById(id));
        return "views/admin/editor-edit";
    }

    @PostMapping("editors/{id}/edit")
    public String editEditor(@PathVariable Integer id, @ModelAttribute User user, Model model) {
        model.addAttribute("editor", this.userService.updateEditor(id, user));
        return "views/admin/editor-edit";
    }

    @GetMapping("customers")
    public String findCustomers(
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            Model model
    ) {
        model.addAttribute("customers",
                this.customerService.findAll(
                        PageRequest.of(0, Integer.MAX_VALUE, Direction.fromString(sortDirection), sortBy.split(","))
                )
        );

        return "views/admin/customer-list";
    }

    @GetMapping("customers/create")
    public String createCustomer(@ModelAttribute("saveCustomerDTO") SaveCustomerDTO saveCustomerDTO) {
        return "views/admin/customer-create";
    }

    @PostMapping("customers/create")
    public String createCustomer(@ModelAttribute("saveCustomerDTO") @Valid SaveCustomerDTO saveCustomerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "views/admin/customer-create";
        }

        this.customerService.save(saveCustomerDTO);
        return "redirect:/admin/customers";
    }

    @GetMapping("customers/{id}/delete")
    public String deleteCustomer(@PathVariable Integer id) {
        this.customerService.delete(id);
        return "redirect:/admin/customers";
    }

    @GetMapping("customers/{id}/edit")
    public String editCustomer(@PathVariable Integer id, Model model) {
        model.addAttribute("saveCustomerDTO", this.customerService.loadSaveCustomerDtoFromCustomer(id));
        return "views/admin/customer-edit";
    }

    @PostMapping("customers/{id}/edit")
    public String editCustomer(@PathVariable Integer id, @ModelAttribute("saveCustomerDTO") @Valid SaveCustomerDTO saveCustomerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "views/admin/customer-edit";
        }

        this.customerService.updateCustomer(id, saveCustomerDTO);
        return "views/admin/customer-edit";
    }

    @GetMapping("newspapers")
    public String newspaperList(
            @ModelAttribute("newspaperToSave") Newspaper newspaper,
            Model model
    ) {
        model.addAttribute("newspapers", this.newspaperService.findAll());
        return "views/admin/newspaper";
    }

    @PostMapping("newspapers")
    public String newspaperSave(
            @ModelAttribute("newspaperToSave") Newspaper newspaper,
            Model model
    ) {

        this.newspaperService.save(newspaper);
        model.addAttribute("newspapers", this.newspaperService.findAll());
        return "views/admin/newspaper";
    }

    @GetMapping("newspapers/{id}/delete")
    public String newspaperList(
            @PathVariable Integer id
    ) {
        this.newspaperService.delete(id);
        return "redirect:/admin/newspapers";
    }

}
