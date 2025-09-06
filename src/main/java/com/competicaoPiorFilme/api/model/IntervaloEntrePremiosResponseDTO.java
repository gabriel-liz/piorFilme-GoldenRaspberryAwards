package com.competicaoPiorFilme.api.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntervaloEntrePremiosResponseDTO {
	
	private List<IntervaloEntrePremiosDTO> min;
	private List<IntervaloEntrePremiosDTO> max;
	
	public IntervaloEntrePremiosResponseDTO(List<IntervaloEntrePremiosDTO> min, List<IntervaloEntrePremiosDTO> max) {		
		this.min = min;
		this.max = max;
	}	
}
