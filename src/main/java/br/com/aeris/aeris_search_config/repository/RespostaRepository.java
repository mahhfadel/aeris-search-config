package br.com.aeris.aeris_search_config.repository;

import br.com.aeris.aeris_search_config.model.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespostaRepository extends JpaRepository<Resposta, Long> {
    List<Resposta> findByPesquisaIdAndUsuarioId(Long pesquisaId, Long usuarioId);

    boolean existsByPesquisaIdAndUsuarioId(Long pesquisaId, Long usuarioId);

    @Modifying
    @Query("DELETE FROM Resposta r WHERE r.pesquisaId = :pesquisaId AND r.usuarioId = :usuarioId")
    void deleteByPesquisaIdAndUsuarioId(@Param("pesquisaId") Long pesquisaId,
                                        @Param("usuarioId") Long usuarioId);
}
