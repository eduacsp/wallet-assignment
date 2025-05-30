package com.eduacsp.walletassignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class WalletAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletAssignmentApplication.class, args);
	}

}
