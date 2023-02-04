package it.xtreamdev.gflbe;

import it.xtreamdev.gflbe.service.ProjectService;
import it.xtreamdev.gflbe.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableAsync
@Slf4j
public class GLFBeApplication implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(GLFBeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String passwordEncoded = this.passwordEncoder.encode("password");
        log.info("Hash for \"password\": {}", passwordEncoded);

        this.projectService.createMissingObjects();
        this.userService.createMissingObjects();
    }
}
