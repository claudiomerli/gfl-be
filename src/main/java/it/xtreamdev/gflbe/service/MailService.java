package it.xtreamdev.gflbe.service;

import it.xtreamdev.gflbe.model.User;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${tilinko.confirm-base-url}")
    private String confirmBaseUrl;

    @Value("${tilinko.confirm-email-base-url}")
    private String confirmChangeEmailBaseUrl;

    @Value("${tilinko.reset-password-base-url}")
    private String resetPasswordBaseUrl;

    public void sendSignupMail(User savedUser) {
        Resource resource = new ClassPathResource("mail-template/confirm-email.html");
        try (InputStream inputStream = resource.getInputStream()) {
            String fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(emailSender.createMimeMessage());
            mimeMessageHelper.setTo(savedUser.getEmail());
            mimeMessageHelper.setSubject("Tilinko - Attivazione account");
            mimeMessageHelper.setText(fileContent
                    .replaceAll("\\$\\$LINK\\$\\$", confirmBaseUrl + savedUser.getActivationCode())
                    .replaceAll("\\$\\$USERNAME\\$\\$", savedUser.getUsername()), true);
            mimeMessageHelper.setFrom("no-reply - Tilinko <tools@tilinkotool.it>");
            this.emailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending email");
        }

    }

    public void sendChangeEmailMail(User user) {
        Resource resource = new ClassPathResource("mail-template/confirm-email-change.html");
        try (InputStream inputStream = resource.getInputStream()) {
            String fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(emailSender.createMimeMessage());
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setSubject("Tilinko - Conferma email");
            mimeMessageHelper.setText(fileContent.replaceAll("\\$\\$LINK\\$\\$", confirmChangeEmailBaseUrl + user.getEmailVerificationCode()), true);
            mimeMessageHelper.setFrom("no-reply - Tilinko <tools@tilinkotool.it>");
            this.emailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending email");
        }
    }

    public void sendResetMail(User user) {
        Resource resource = new ClassPathResource("mail-template/reset-password.html");
        try (InputStream inputStream = resource.getInputStream()) {
            String fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(emailSender.createMimeMessage());
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setSubject("Tilinko - Reset Password");
            mimeMessageHelper.setText(fileContent.replaceAll("\\$\\$LINK\\$\\$", resetPasswordBaseUrl + user.getResetPasswordCode()), true);
            mimeMessageHelper.setFrom("no-reply - Tilinko <tools@tilinkotool.it>");
            this.emailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending email");
        }
    }
}
