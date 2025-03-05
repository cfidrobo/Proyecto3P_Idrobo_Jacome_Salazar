package ec.edu.espe.entrevistas_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EntrevistasServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EntrevistasServiceApplication.class, args);
	}

}
