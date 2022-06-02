package com.example.uploaddownload.controllers;

import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;
import com.example.uploaddownload.model.UploadFileResponse;
import com.example.uploaddownload.service.FileStorageService;

@RestController
public class FileController {
	// criar uma propriedade para servir de log de manipulacao a partir do controller
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	
	@Autowired
	private FileStorageService fileStorageService;
	
	// uso da annotation @PostMapping
	@PostMapping("/uploadFile")
	public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
		String fileName = fileStorageService.storeFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path("/downloadFile/")
				.path(fileName)
				.toUriString();
		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}
	// metodo para uppar 2 ou mais arquivos
	@PostMapping("/uploadMultiplesFiles")
	public List<UploadFileResponse> uploadMultiplesFiles(@RequestParam("files")MultipartFile[] files){
		return Arrays.asList(files)
				.stream()
				.map(file -> uploadFile(file))
				.collect(Collectors.toList());
	}
	// criar a estrutura logica que possibilitara o download de arquivo
	@GetMapping("/downloadFile/{fileName: +}")
	public ResponseEntity<Resource>downloadFile(
			@PathVariable String fileName, HttpServletRequest request){
		// criar uma propriedade para acessar o metodo que executa os downs
		Resource resource = fileStorageService.loadFileAsResourse(fileName);
		
		// tentativa de determinar o tipo de conteudo do arquivo
		String contentType = null;
		
		// bloco try/catch
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		}catch(IOException ex) {
			logger.info("Nao foi possivel determinar o tipo do conteudo do arquivo.");
		}
	// verificar se o tipo de conteudo nao pode ser verificado porque a variavel nao conseguiu acessar
		if(contentType == null) {
			contentType = "application/octet-stream";
		}
	// implementacao da declaracao
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename-\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}