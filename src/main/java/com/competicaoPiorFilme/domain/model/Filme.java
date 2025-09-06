package com.competicaoPiorFilme.domain.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Filme {

	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(nullable = false)
	private String titulo;
	@Column(nullable = false)
	private int ano;

	private boolean premiado;
	 
	@ManyToMany
	@JoinTable(
			name = "filmeProdutor",
			joinColumns = @JoinColumn(name = "filme_id"),
			inverseJoinColumns = @JoinColumn(name= "produtor_id"))
	private Set<Produtor> produtores = new HashSet<>();
	
	@ManyToMany
	@JoinTable(
			name = "filme_studio",
			joinColumns = @JoinColumn(name= "filme_id"),
			inverseJoinColumns = @JoinColumn(name = "studio_id"))	
	private Set<Estudio> estudios = new HashSet<>();

//	public boolean getPremiado() {
//		return premiado;
//	}
	
}
