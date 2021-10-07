package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.model.Attachment;
import it.xtreamdev.gflbe.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    public Attachment findById(Integer id) {
        return this.attachmentRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "Attachment not found"));
    }

}
