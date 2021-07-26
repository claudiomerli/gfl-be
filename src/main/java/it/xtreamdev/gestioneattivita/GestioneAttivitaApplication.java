package it.xtreamdev.gestioneattivita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GestioneAttivitaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestioneAttivitaApplication.class, args);
    }

}
