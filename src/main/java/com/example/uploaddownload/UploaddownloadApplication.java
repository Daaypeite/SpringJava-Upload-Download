package com.example.uploaddownload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.uploaddownload.property.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class })

public class UploaddownloadApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploaddownloadApplication.class, args);
	}

}
