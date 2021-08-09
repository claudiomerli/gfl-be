package it.xtreamdev.gflbe.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@Service
@Slf4j
public class FtpService {

    @Value("${gestionalecontenuti.ftp.host}")
    private String host;

    @Value("${gestionalecontenuti.ftp.user}")
    private String user;

    @Value("${gestionalecontenuti.ftp.password}")
    private String password;

    public void storeFile(String fileName, String clientName, ByteArrayOutputStream outputStream) throws IOException {
        try {
            LocalDate now = LocalDate.now();

            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(host);
            ftpClient.login(user, password);


            ftpClient.makeDirectory("exports/" + clientName);
            ftpClient.makeDirectory("exports/" + clientName + "/" + now.getMonth().getValue());
            ftpClient.makeDirectory("exports/" + clientName + "/" + now.getMonth().getValue() + "/" + now.getDayOfMonth());
            ftpClient.storeFile("exports/" + clientName + "/" + now.getMonth().getValue() + "/" + now.getDayOfMonth() + "/" + fileName, new ByteArrayInputStream(outputStream.toByteArray()));
            log.info("File uploaded");
        } catch (Exception e) {
            log.error("Error uploading file", e);
        }
    }

}
