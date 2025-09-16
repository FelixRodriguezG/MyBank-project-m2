package io.github.felix.bank_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankBackApplication {
	// DOC: http://localhost:8080/swagger-ui.html
	public static void main(String[] args) {
		SpringApplication.run(BankBackApplication.class, args);
	}

}
