package com.springboot.invoiceparser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"controller", "service", "dto", "exception", "util", "com.springboot.invoiceparser","test"})
public class InvoiceParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceParserApplication.class, args);
	}
}

