package com.competicaoPiorFilme.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.competicaoPiorFilme.domain.model.Produtor;

@Repository
public interface ProdutorRepository extends JpaRepository<Produtor, Long> {
	 Optional<Produtor> findByNome(String nome);
}
