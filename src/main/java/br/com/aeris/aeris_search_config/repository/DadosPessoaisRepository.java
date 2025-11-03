package br.com.aeris.aeris_search_config.repository;

import br.com.aeris.aeris_search_config.model.DadosPessoais;
import br.com.aeris.aeris_search_config.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DadosPessoaisRepository extends JpaRepository<DadosPessoais, Long> {
    DadosPessoais findByUsuario(Usuario usuario);

    boolean existsByUsuario(Usuario usuario);
}
