package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.demo.dao.mapper")
public class FtpDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FtpDemoApplication.class, args);
	}
}
