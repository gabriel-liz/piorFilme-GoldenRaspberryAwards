package com.competicaoPiorFilme.api.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilmeDTO {
	
	private Long id;
	private String titulo;
	private boolean premiado;
	private Integer ano;	
	private List<ProdutorDTO> produtores;
	private List<EstudioDTO> estudios;
	
	
	
	
}
