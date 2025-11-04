package br.com.aeris.aeris_search_config.repository;

import br.com.aeris.aeris_search_config.model.Pergunta;
import br.com.aeris.aeris_search_config.model.TipoPergunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoPerguntaRepository extends JpaRepository<TipoPergunta, Long> {
    TipoPergunta findByPergunta(Pergunta pergunta);
}
