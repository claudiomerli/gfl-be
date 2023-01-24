package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    public byte[] findById(Integer id){
        return this.attachmentRepository.findById(id).orElseThrow().getPayload();
    }

}
