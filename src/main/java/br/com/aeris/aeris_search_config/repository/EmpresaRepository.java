package br.com.aeris.aeris_search_config.repository;

import br.com.aeris.aeris_search_config.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

}
