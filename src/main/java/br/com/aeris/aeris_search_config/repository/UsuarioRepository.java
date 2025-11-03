package br.com.aeris.aeris_search_config.repository;

import br.com.aeris.aeris_search_config.model.Empresa;
import br.com.aeris.aeris_search_config.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);

    List<Usuario> findByEmpresa(Empresa empresa);

    boolean existsByEmail(String email);
}

