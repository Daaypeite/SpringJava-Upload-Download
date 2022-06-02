package com.example.uploaddownload.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.uploaddownload.exception.FileStorageException;
import com.example.uploaddownload.exception.MyFileNotFoundExcepetion;
import com.example.uploaddownload.property.FileStorageProperties;

@Service
public class FileStorageService {

	// atributo para assumir o valor do path
	private final Path fileStorageLocation;

	// constructor - com injencao de dependencia
	@Autowired
	public FileStorageService(FileStorageProperties FileStorageProperties) {
		this.fileStorageLocation = Paths.get(FileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		// bloco try/catch
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Nao foi possivel criar o diretorio no local indicado para o upload", ex);
		}
	}

	// metodo que acessa o arquivo - a partir de sua identificacao
	public String storeFile(MultipartFile File) {
		// tratamento/normalizacao do path para acessar o arquivo
		String fileName = StringUtils.cleanPath(File.getOriginalFilename());

		// bloco try/catch
		try {
			if (fileName.contains("..")) {
				throw new FileStorageException("O arquivo contem uma sequencia invalida para o caminho! " + fileName);
			}
			// copiar o arquivo para o local indicado(caso exista um arquivo com o mesmo
			// nome, sera substituido)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(File.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new FileStorageException("Nao foi possivel armazenar o arquivo " + fileName + ". Tente Novamente!",
					ex);
		}
		return fileName;
	}

	// tentativa de recuperar o arquivo uppado
	public Resource loadFileAsResourse(String fileName) {
		// bloco try/catch
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());

			// verificar se o recurso existe
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundExcepetion("Arquivo nao encontrado. " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundExcepetion("Arquivo nao encontrado. " + fileName, ex);
		}
	}

}
