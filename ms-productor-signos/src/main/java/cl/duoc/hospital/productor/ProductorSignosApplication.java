package cl.duoc.hospital.productor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProductorSignosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductorSignosApplication.class, args);
    }
}
