package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.editor.EditEditorDTO;
import it.xtreamdev.gflbe.dto.editor.SaveEditorDTO;
import it.xtreamdev.gflbe.model.User;
import it.xtreamdev.gflbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/editor")
public class EditorController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<User>> findEditors(
            @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "globalSearch", required = false) String gloabalSearch
    ) {
        Page<User> result = this.userService
                .findEditors(gloabalSearch, PageRequest.of(page, pageSize, Sort.Direction.fromString(sortDirection), sortBy.split(",")));

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> createEditor(@RequestBody SaveEditorDTO editor) {
        this.userService.createEditor(editor);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteEditor(@PathVariable Integer id) {
        this.userService.deleteEditor(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<User> editEditor(@PathVariable Integer id, @RequestBody EditEditorDTO editEditorDTO) {
        return ResponseEntity.ok(this.userService.updateEditor(id, editEditorDTO));
    }

    @GetMapping("{id}")
    public ResponseEntity<User> editEditor(@PathVariable Integer id) {
        return ResponseEntity.ok(this.userService.findById(id));
    }


}
