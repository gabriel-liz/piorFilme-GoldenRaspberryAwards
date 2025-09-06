package com.competicaoPiorFilme.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.competicaoPiorFilme.domain.model.Filme;

public interface FilmeRepository extends JpaRepository<Filme, Long> {
	Optional<Filme> findByTituloAndAno(String titulo, Integer ano);	
}
