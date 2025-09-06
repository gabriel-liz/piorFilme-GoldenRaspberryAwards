package com.competicaoPiorFilme.api.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.competicaoPiorFilme.api.model.FilmeDTO;
import com.competicaoPiorFilme.domain.model.Filme;

@Component
public class FilmeDTOAssembler {

	private final ProdutorDTOAssembler produtorAssembler;
	private final EstudioDTOAssembler estudioAssembler;

	public FilmeDTOAssembler(ProdutorDTOAssembler produtorAssembler, EstudioDTOAssembler estudioAssembler) {
		this.produtorAssembler = produtorAssembler;
		this.estudioAssembler = estudioAssembler;
	}

	public FilmeDTO toDTO(Filme filme) {
		FilmeDTO dto = new FilmeDTO();
		dto.setId(filme.getId());
		dto.setTitulo(filme.getTitulo());
		dto.setAno(filme.getAno());
		dto.setPremiado(filme.isPremiado());
		dto.setProdutores(filme.getProdutores()
				.stream()
				.map(produtorAssembler::toDTO)
				.collect(Collectors.toList()));
		dto.setEstudios(filme.getEstudios()
				.stream().map(estudioAssembler::toDTO)
				.collect(Collectors.toList()));
		return dto;
	}
	
	public List<FilmeDTO> toCollectionDTO(List<Filme> filmes){
		return filmes.stream()
				.map(filme -> toDTO(filme))
				.collect(Collectors.toList());
	}
}
