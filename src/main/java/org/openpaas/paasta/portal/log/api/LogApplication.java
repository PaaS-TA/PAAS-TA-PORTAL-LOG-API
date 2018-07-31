package org.openpaas.paasta.portal.log.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;



@SpringBootApplication
@ComponentScan(basePackages = {"org.openpaas.paasta"})
public class LogApplication {
	public static void main(String[] args) {
		SpringApplication.run(LogApplication.class, args);
	}
}