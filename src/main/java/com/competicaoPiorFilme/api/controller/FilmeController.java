package com.competicaoPiorFilme.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.competicaoPiorFilme.api.assembler.FilmeDTOAssembler;
import com.competicaoPiorFilme.api.model.FilmeDTO;
import com.competicaoPiorFilme.api.model.IntervaloEntrePremiosResponseDTO;
import com.competicaoPiorFilme.domain.model.Filme;
import com.competicaoPiorFilme.domain.repository.FilmeRepository;
import com.competicaoPiorFilme.domain.service.IntervaloEntrePremiosService;
import com.competicaoPiorFilme.domain.service.importacaobasededados.CsvImportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/filmes")
@RequiredArgsConstructor 
public class FilmeController {

    private final FilmeRepository filmeRepository;
    private final FilmeDTOAssembler filmeAssembler;    
    private final CsvImportService csvImportService;
    private final IntervaloEntrePremiosService intervaloEntrePremiosService;

    @GetMapping
    public List<FilmeDTO> listar() {
        List<Filme> todosFilmes = filmeRepository.findAll();
        return filmeAssembler.toCollectionDTO(todosFilmes);
    }

    @PostMapping(value = "/importar", consumes = "multipart/form-data")
    public ResponseEntity<String> importarCsv(@RequestParam("file") MultipartFile file) {
        try {
            csvImportService.importarCsv(file);
            return ResponseEntity.ok("Importação realizada com sucesso");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao importar CSV: " + e.getMessage());
        }
    }

    @GetMapping("/premios/intervalo")
    public ResponseEntity<IntervaloEntrePremiosResponseDTO> buscarIntervalosPremios() {
        IntervaloEntrePremiosResponseDTO response = intervaloEntrePremiosService.calcularIntervaloPremios();
        return ResponseEntity.ok(response);
    }
}
