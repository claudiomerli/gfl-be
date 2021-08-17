package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.SaveEditorDTO;
import it.xtreamdev.gflbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/editor")
public class EditorController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Void> createEditor(@RequestBody SaveEditorDTO editor) {
        this.userService.createEditor(editor);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
