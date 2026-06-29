package cl.duoc.hospital.signosvitales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsSignosVitalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSignosVitalesApplication.class, args);
    }
}
