package com.competicaoPiorFilme.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntervaloEntrePremiosDTO {

	private String producer;
	private int interval;
	private int previousWin;
	private int followingWin;

	public IntervaloEntrePremiosDTO(String producer, int interval, int previousWin, int followingWin) {

		this.producer = producer;
		this.interval = interval;
		this.previousWin = previousWin;
		this.followingWin = followingWin;
	}

}
