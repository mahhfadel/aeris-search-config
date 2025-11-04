package br.com.aeris.aeris_search_config.repository;

import br.com.aeris.aeris_search_config.model.Empresa;
import br.com.aeris.aeris_search_config.model.Pergunta;
import br.com.aeris.aeris_search_config.model.Pesquisa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerguntaRepository extends JpaRepository<Pergunta, Long> {
    List<Pergunta> findByPesquisa(Pesquisa pesquisa);

}

