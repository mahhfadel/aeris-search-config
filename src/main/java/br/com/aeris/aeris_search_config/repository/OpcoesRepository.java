package br.com.aeris.aeris_search_config.repository;

import br.com.aeris.aeris_search_config.model.Opcoes;
import br.com.aeris.aeris_search_config.model.PesquisaColaborador;
import br.com.aeris.aeris_search_config.model.TipoPergunta;
import br.com.aeris.aeris_search_config.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpcoesRepository extends JpaRepository<Opcoes, Long> {
    List<Opcoes> findByTipoPergunta(TipoPergunta tipoPergunta);
}
