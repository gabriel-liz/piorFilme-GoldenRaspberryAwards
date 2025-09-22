package com.competicaoPiorFilme.domain.config;

import java.io.InputStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.competicaoPiorFilme.domain.service.importacaobasededados.CsvImportService;

@Component
public class DataInitializer implements CommandLineRunner {

	private final CsvImportService csvImportService;

	public DataInitializer(CsvImportService csvImportService) {
		this.csvImportService = csvImportService;
	}

	@Override
	public void run(String... args) throws Exception {

		ClassPathResource resource = new ClassPathResource("Movielist.csv");

		try (InputStream inputStream = resource.getInputStream()) {
			csvImportService.importarCsv(inputStream);
			System.out.println("CSV importado com sucesso na inicialização.");
		}catch (Exception e) {
			System.out.println("Erro ao importar CSV na inicialização: " + e.getMessage());
			e.printStackTrace();
		}

	}
}
