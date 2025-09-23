package com.competicaoPiorFilme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    @BeforeEach
    void setup() throws Exception {
        filmeRepository.deleteAll();
        InputStream inputStream = new ClassPathResource("filmes-teste.csv").getInputStream();
        csvImportService.importarCsv(inputStream);
    }

    /*
    Requisito não funcional 2: O teste de integração deve garantir que os dados retornados na API estão de acordo com o conteúdo do arquivo padrão,
    e deve falhar se o arquivo for modificado de forma que qualquer aspecto do resultado mude;
    */

    @Test
    void deveRetornarResultadoExatoDoCsvPadrao() throws IOException {
        IntervaloEntrePremiosResponseDTO response = intervaloService.calcularIntervaloPremios();
        InputStream arquivoJsonEsperado = new ClassPathResource("intervalo-resultado-esperado.json").getInputStream();
        String jsonEsperado = new String(arquivoJsonEsperado.readAllBytes(), StandardCharsets.UTF_8);
        String jsonAtual = objectMapper.writeValueAsString(response);
        assertThat(jsonAtual).isEqualToIgnoringWhitespace(jsonEsperado);
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
                tuple("Matthew Vaughn", 13) 
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
