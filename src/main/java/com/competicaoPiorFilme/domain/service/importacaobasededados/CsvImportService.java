package com.competicaoPiorFilme.domain.service.importacaobasededados;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.competicaoPiorFilme.domain.exception.CsvValidationException;
import com.competicaoPiorFilme.domain.model.Estudio;
import com.competicaoPiorFilme.domain.model.Filme;
import com.competicaoPiorFilme.domain.model.Produtor;
import com.competicaoPiorFilme.domain.repository.EstudioRepository;
import com.competicaoPiorFilme.domain.repository.FilmeRepository;
import com.competicaoPiorFilme.domain.repository.ProdutorRepository;

@Service
public class CsvImportService {
	
	private static final List<String> CABECALHO_ESPERADO = List.of("year", "title", "studios", "producers", "winner");	
	
	 private final FilmeRepository filmeRepository;
	 private final ProdutorRepository produtorRepository;
	 private final EstudioRepository estudioRepository;

	public CsvImportService(FilmeRepository filmeRepository, ProdutorRepository produtorRepository,
			EstudioRepository estudioRepository) {
		this.filmeRepository = filmeRepository;
		this.produtorRepository = produtorRepository;
		this.estudioRepository = estudioRepository;
	}

	public void importarCsv(MultipartFile file) throws Exception {
		validarArquivo(file);
		importarCsv(file.getInputStream());
	}
	
	@Transactional
	public void importarCsv(InputStream inputStream) throws Exception {
		try (BufferedReader fileReader = new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

			CSVFormat format = CSVFormat.DEFAULT
					.builder()
					.setHeader()
					.setSkipHeaderRecord(true)
					.setDelimiter(';')
					.build();

			try (CSVParser csvParser = new CSVParser(fileReader, format)) {
				validarCabecalho(csvParser.getHeaderNames());
				for (CSVRecord record : csvParser) {					
					processarRegistros(record);
				}
			} 
		}catch (CsvValidationException e) {
			throw e;
		} catch (Exception e) {
			throw new CsvValidationException("Erro ao processar o arquivo CSV: " + e.getMessage());
		}
	}

	private List<String> splitNomes(String nomeStr) {
		String normalizado = nomeStr.replaceAll(",\\s*and\\s*", ",");
		return Arrays.stream(normalizado.split(",|\\band\\b"))
				.map(String::trim)
				.filter(nome -> !nome.isEmpty())
				.toList();
	}

	private void processarRegistros(CSVRecord record) {
	    Integer ano = Integer.valueOf(record.get("year").trim());
	    String titulo = record.get("title").trim();
	    String studiosStr = record.get("studios").trim();
	    String producersStr = record.get("producers").trim();
	    String winnerStr = record.isMapped("winner") ? record.get("winner").trim() : "";	

	    boolean premiado = winnerStr != null && winnerStr.trim().equalsIgnoreCase("yes");

	    Optional<Filme> existente = filmeRepository.findByTituloAndAno(titulo, ano);
	    Filme filme = existente.orElseGet(() -> {
	        Filme novo = new Filme();
	        novo.setAno(ano);
	        novo.setTitulo(titulo);
			novo.setPremiado(premiado);
	        return novo;
	    });

	    Set<Estudio> estudios = new HashSet<>();
	    for (String nomeEstudio : splitNomes(studiosStr)) {
	        Estudio estudio = estudioRepository.findByNome(nomeEstudio)
	            .orElseGet(() -> {
	                Estudio novo = new Estudio();
	                novo.setNome(nomeEstudio);
	                return estudioRepository.save(novo);
	            });
	        estudios.add(estudio);
	    }
	    filme.setEstudios(estudios);

	    Set<Produtor> produtores = new HashSet<>();
	    for (String nomeProdutor : splitNomes(producersStr)) {
	        Produtor produtor = produtorRepository.findByNome(nomeProdutor)
	            .orElseGet(() -> {
	                Produtor novo = new Produtor();
	                novo.setNome(nomeProdutor);
	                return produtorRepository.save(novo);
	            });
	        produtores.add(produtor);
	    }

	    filme.setProdutores(produtores);
		filme.setPremiado(premiado);
	    filmeRepository.save(filme);
	}
	
    private void validarArquivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CsvValidationException("Nenhum arquivo foi enviado.");
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new CsvValidationException("O arquivo enviado não é um .csv válido.");
        }
    }
    
    private void validarCabecalho(List<String> cabecalho) {
        if (cabecalho.size() != CABECALHO_ESPERADO.size()) {
            throw new CsvValidationException("Cabeçalho do CSV inválido. Esperado: " + CABECALHO_ESPERADO);
        }

        for (int i = 0; i < cabecalho.size(); i++) {
            if (!cabecalho.get(i).trim().equalsIgnoreCase(CABECALHO_ESPERADO.get(i))) {
                throw new CsvValidationException("Cabeçalho inválido. Esperado: " + CABECALHO_ESPERADO);
            } 
        }
    }     

}
