package com.competicaoPiorFilme.api.assembler;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.competicaoPiorFilme.api.model.ProdutorDTO;
import com.competicaoPiorFilme.domain.model.Produtor;

@Component
public class ProdutorDTOAssembler {
	
	private final ModelMapper modelMapper;

    public ProdutorDTOAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
	
	
	public ProdutorDTO toDTO(Produtor produtor) {
		return modelMapper.map(produtor, ProdutorDTO.class);
	}
	
	public List<ProdutorDTO> toCollectionDTO(List<Produtor> produtores){
		return produtores.stream()
				.map(produtor -> toDTO(produtor))
				.collect(Collectors.toList());
	}
}
