package com.competicaoPiorFilme.domain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.competicaoPiorFilme.api.model.IntervaloEntrePremiosDTO;
import com.competicaoPiorFilme.api.model.IntervaloEntrePremiosResponseDTO;
import com.competicaoPiorFilme.domain.model.Filme;
import com.competicaoPiorFilme.domain.repository.FilmeRepository;

@Service
public class IntervaloEntrePremiosService {

	private final FilmeRepository filmeRepository;
	
    public IntervaloEntrePremiosService(FilmeRepository filmeRepository) {
        this.filmeRepository = filmeRepository;
    }

    public IntervaloEntrePremiosResponseDTO calcularIntervaloPremios() {
        List<Filme> filmesPremiados = getFilmesPremiados();        
        Map<String, List<Integer>> premiosPorProdutor = getPremiosPorProdutos(filmesPremiados);
        List<IntervaloEntrePremiosDTO> intervalos = new ArrayList<>();
        
        for (Map.Entry<String, List<Integer>> entry : premiosPorProdutor.entrySet()) {
            String produtor = entry.getKey();
            List<Integer> anos = entry.getValue();

            if (anos.size() < 2) continue;

            Collections.sort(anos);

            for (int i = 1; i < anos.size(); i++) {
                int intervalo = anos.get(i) - anos.get(i - 1);
                intervalos.add(new IntervaloEntrePremiosDTO(
                        produtor,
                        intervalo,
                        anos.get(i - 1),
                        anos.get(i)
                ));
            }
        }

        if (intervalos.isEmpty()) {
            return new IntervaloEntrePremiosResponseDTO(List.of(), List.of());
        }


        int menorIntervalo = getMenorIntervalo(intervalos);

        int maiorIntervalo = getMaiorIntervalo(intervalos);

        List<IntervaloEntrePremiosDTO> listarMenorIntervalo = listarIntervalos(intervalos, menorIntervalo);

        List<IntervaloEntrePremiosDTO> listarMaiorIntervalo = listarIntervalos(intervalos, maiorIntervalo);

        return new IntervaloEntrePremiosResponseDTO(listarMenorIntervalo, listarMaiorIntervalo);
    }

	private List<IntervaloEntrePremiosDTO> listarIntervalos(List<IntervaloEntrePremiosDTO> intervalos,
			int minIntervalo) {
		return intervalos.stream()
                .filter(i -> i.getInterval() == minIntervalo)
                .collect(Collectors.toList());
	}

	private int getMaiorIntervalo(List<IntervaloEntrePremiosDTO> intervalos) {
		return intervalos.stream()
                .mapToInt(IntervaloEntrePremiosDTO::getInterval)
                .max()
                .orElse(0);
	}

	private int getMenorIntervalo(List<IntervaloEntrePremiosDTO> intervalos) {
		return intervalos.stream()
                .mapToInt(IntervaloEntrePremiosDTO::getInterval)
                .min()
                .orElse(0);
	}
    
    public List<Filme> getFilmesPremiados(){
    	return filmeRepository.findAll().stream()
        .filter(Filme::isPremiado)
        .collect(Collectors.toList());
    }
    
    public Map<String, List<Integer>> getPremiosPorProdutos(List<Filme> filmesPremiados) {
    	return filmesPremiados.stream()
        .flatMap(f -> f.getProdutores()
                .stream()
                .map(p -> Map.entry(p.getNome(), f.getAno())))
        .collect(Collectors.groupingBy(
        		Map.Entry::getKey,
        		Collectors.mapping(Map.Entry::getValue, Collectors.toList())
        ));
    }
}
