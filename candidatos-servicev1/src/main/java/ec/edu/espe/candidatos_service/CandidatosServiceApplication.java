package ec.edu.espe.candidatos_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CandidatosServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CandidatosServiceApplication.class, args);
	}

}
