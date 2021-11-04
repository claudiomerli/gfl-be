package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.model.Content;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
@Slf4j
public class ContentMailService {

    @Autowired
    private JavaMailSender emailSender;

    @Async
    public void sendCreationMail(Content content) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("Gestionale Contenuti <info@tilinkotool.it>");
            mimeMessageHelper.setTo(content.getEditor().getEmail());
            mimeMessageHelper.setSubject("Nuovo contenuto assegnato");
            mimeMessageHelper.setText(String.format("Ti è stato assegnato il contenuto \"%s\" con id %s", content.getTitle(), content.getId()));


            this.emailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Error sending email", e);
        }
    }

    @Async
    public void sendUpdateMail(Content content) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("Gestionale Contenuti <info@tilinkotool.it>");
            mimeMessageHelper.setTo(content.getEditor().getEmail());
            mimeMessageHelper.setSubject("Stato contenuto aggiornato");
            mimeMessageHelper.setText(String.format("Il contenuto \"%s\" è passato in stato %s", content.getTitle(), content.getContentStatus()));


            this.emailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Error sending email", e);
        }
    }

}
