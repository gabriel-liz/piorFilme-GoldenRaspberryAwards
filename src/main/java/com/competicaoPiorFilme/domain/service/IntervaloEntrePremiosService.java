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
        List<Filme> filmesPremiados = filmeRepository.findAllByPremiadoTrue();
        Map<String, List<Integer>> premiosPorProdutor = getPremiosPorProdutor(filmesPremiados);

        List<IntervaloEntrePremiosDTO> intervalos = calcularIntervalos(premiosPorProdutor);

        if (intervalos.isEmpty()) {
            return new IntervaloEntrePremiosResponseDTO(List.of(), List.of());
        }

        return extrairMinMax(intervalos);
    }

    private List<IntervaloEntrePremiosDTO> calcularIntervalos(Map<String, List<Integer>> premiosPorProdutor) {
        List<IntervaloEntrePremiosDTO> intervalos = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : premiosPorProdutor.entrySet()) {
            List<Integer> anos = entry.getValue().stream().sorted().toList();
            for (int i = 1; i < anos.size(); i++) {
                intervalos.add(new IntervaloEntrePremiosDTO(
                        entry.getKey(),
                        anos.get(i) - anos.get(i - 1),
                        anos.get(i - 1),
                        anos.get(i)
                ));
            }
        }
        return intervalos;
    }

    private IntervaloEntrePremiosResponseDTO extrairMinMax(List<IntervaloEntrePremiosDTO> intervalos) {
        int menor = Integer.MAX_VALUE;
        int maior = Integer.MIN_VALUE;
        List<IntervaloEntrePremiosDTO> minList = new ArrayList<>();
        List<IntervaloEntrePremiosDTO> maxList = new ArrayList<>();

        for (IntervaloEntrePremiosDTO dto : intervalos) {
            int val = dto.getInterval();

            if (val < menor) {
                menor = val;
                minList.clear();
                minList.add(dto);
            } else if (val == menor) {
                minList.add(dto);
            }

            if (val > maior) {
                maior = val;
                maxList.clear();
                maxList.add(dto);
            } else if (val == maior) {
                maxList.add(dto);
            }
        }

        return new IntervaloEntrePremiosResponseDTO(minList, maxList);
    }

    private Map<String, List<Integer>> getPremiosPorProdutor(List<Filme> filmesPremiados) {
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
