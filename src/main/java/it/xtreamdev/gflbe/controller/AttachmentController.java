package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.model.Attachment;
import it.xtreamdev.gflbe.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @GetMapping("{id}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Integer id) {
        Attachment attachment = this.attachmentService.findById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, attachment.getContentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;filename=\"%s\"", attachment.getFilename()))
                .body(attachment.getAttachmentData().getBytes());
    }

}
