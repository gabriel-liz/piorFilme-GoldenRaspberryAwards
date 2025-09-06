package com.competicaoPiorFilme.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.competicaoPiorFilme.domain.model.Estudio;

public interface EstudioRepository extends JpaRepository<Estudio, Long> {
	 Optional<Estudio> findByNome(String nome);
}
