package com.competicaoPiorFilme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.competicaoPiorFilme.api.model.IntervaloEntrePremiosResponseDTO;
import com.competicaoPiorFilme.domain.model.Filme;
import com.competicaoPiorFilme.domain.repository.FilmeRepository;
import com.competicaoPiorFilme.domain.service.IntervaloEntrePremiosService;
import com.competicaoPiorFilme.domain.service.importacaobasededados.CsvImportService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FilmeIntegrationTest {

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private IntervaloEntrePremiosService intervaloService;

    @BeforeEach
    void setup() throws Exception {
        filmeRepository.deleteAll();
        InputStream inputStream = new ClassPathResource("filmes-teste.csv").getInputStream();
        csvImportService.importarCsv(inputStream);
    }

    @Test
    void deveCarregarFilmesPremiadosCorretamente() {
        List<Filme> filmes = filmeRepository.findAll();
        
        assertThat(filmes).isNotEmpty();
        
        assertThat(filmes)
            .filteredOn(Filme::isPremiado)
            .isNotEmpty();
        
        assertThat(filmes)
            .filteredOn(f -> !f.isPremiado())
            .isNotEmpty();
    }

    @Test
    void deveCalcularIntervalosEntrePremiosDosProdutores() {
        IntervaloEntrePremiosResponseDTO response = intervaloService.calcularIntervaloPremios();

        assertThat(response.getMin()).isNotEmpty();
        assertThat(response.getMax()).isNotEmpty();        
        assertThat(response.getMin())
            .extracting("producer", "interval")
            .containsExactlyInAnyOrder(
                tuple("Joel Silver", 1) 
            );
        
        assertThat(response.getMax())
            .extracting("producer", "interval")
            .containsExactlyInAnyOrder(
                tuple("Buzz Feitshans", 9) 
            );
    }
    
    @Test
    void deveRetornarRespostaNoFormatoEsperado() {
        IntervaloEntrePremiosResponseDTO response = intervaloService.calcularIntervaloPremios();

        assertThat(response.getMin()).isNotNull();
        assertThat(response.getMax()).isNotNull();

        assertThat(response.getMin())
            .allSatisfy(item -> {
                assertThat(item.getProducer()).isNotNull();
                assertThat(item.getInterval()).isNotNull();
                assertThat(item.getPreviousWin()).isNotNull();
                assertThat(item.getFollowingWin()).isNotNull();
            });

        assertThat(response.getMax())
            .allSatisfy(item -> {
                assertThat(item.getProducer()).isNotNull();
                assertThat(item.getInterval()).isNotNull();
                assertThat(item.getPreviousWin()).isNotNull();
                assertThat(item.getFollowingWin()).isNotNull();
            });
    }
}
