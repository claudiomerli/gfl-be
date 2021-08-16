package it.xtreamdev.gflbe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@SpringBootApplication
@Slf4j
public class GLFBeApplication implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(GLFBeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String passwordEncoded = this.passwordEncoder.encode("password");
        log.info("Hash for \"password\": {}", passwordEncoded);
    }
}
