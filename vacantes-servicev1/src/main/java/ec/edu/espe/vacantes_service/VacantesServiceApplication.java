package ec.edu.espe.vacantes_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VacantesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VacantesServiceApplication.class, args);
	}

}
