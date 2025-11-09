package br.com.aeris.aeris_search_config.service;

import br.com.aeris.aeris_search_config.dto.PesquisaResponse;
import br.com.aeris.aeris_search_config.model.Pesquisa;
import br.com.aeris.aeris_search_config.model.PesquisaColaborador;
import br.com.aeris.aeris_search_config.model.Usuario;
import br.com.aeris.aeris_search_config.repository.EmpresaRepository;
import br.com.aeris.aeris_search_config.repository.PesquisaColaboradorRepository;
import br.com.aeris.aeris_search_config.repository.PesquisaRepository;
import br.com.aeris.aeris_search_config.repository.UsuarioRepository;
import br.com.aeris.aeris_search_config.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class PesquisasService {
    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private PesquisaColaboradorRepository pesquisaColaboradorRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(PesquisasService.class);

    @Transactional
    public PesquisaResponse createPesquisa(String token){
        logger.info("Iniciando criação de nova pesquisa com token recebido");

        Usuario usuario = usuarioRepository.findByEmail(jwtUtil.extractEmail(token));
        logger.debug("Usuário autenticado: {}", usuario.getEmail());

        if (!usuario.getAtivo()) {
            logger.warn("Tentativa de criação de pesquisa por usuário inativo: {}", usuario.getEmail());
            throw new IllegalArgumentException("Usuário inativo");
        }

        if (!Objects.equals(usuario.getTipo(), "adm")) {
            logger.warn("Tentativa de criação de pesquisa por usuário não administrador: {}", usuario.getEmail());
            throw new IllegalArgumentException("Usuário não tem acesso");
        }

        List<Pesquisa> quantPesquisasEmpresa = pesquisaRepository.findByEmpresa(usuario.getEmpresa());
        logger.debug("Total de pesquisas existentes para empresa {}: {}", usuario.getEmpresa().getNome(), quantPesquisasEmpresa.size());

        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setCriadoEm(LocalDate.now());
        pesquisa.setPrazo(LocalDate.now().plus(Period.ofDays(30)));
        pesquisa.setNome(String.format("Pesquisa #%03d", (long) quantPesquisasEmpresa.size() + 1));
        pesquisa.setUsuarioId(usuario.getId());
        pesquisa.setAtivo(true);
        pesquisa.setEmpresa(usuario.getEmpresa());

        Pesquisa pesquisaCriada = pesquisaRepository.save(pesquisa);
        logger.info("Pesquisa criada com sucesso: {}", pesquisaCriada.getNome());

        return PesquisaResponse.builder()
                .idPesquisa(pesquisaCriada.getId())
                .nome(pesquisaCriada.getNome())
                .mensagem("Pesquisa criada com sucesso")
                .build();
    }

    public List<PesquisaResponse> getAllPesquisas(Long empresa){
        logger.info("Buscando todas as pesquisas da empresa {}", empresa);

        List<Pesquisa> pesquisas = pesquisaRepository.findByEmpresa(empresaRepository.getReferenceById(empresa));

        if(pesquisas == null){
            logger.warn("Nenhuma pesquisa encontrada para empresa {}", empresa);
            throw new EntityNotFoundException("Essa empresa ainda não possui pesquisas");
        }

        logger.debug("Total de pesquisas encontradas: {}", pesquisas.size());
        List<PesquisaResponse> responses = new ArrayList<>();

        for(Pesquisa pesquisa: pesquisas){
            List<PesquisaColaborador> usuariosAssociados = pesquisaColaboradorRepository.findByPesquisa(pesquisa);

            PesquisaResponse response = PesquisaResponse.builder()
                    .idPesquisa(pesquisa.getId())
                    .nome(pesquisa.getNome())
                    .criadoEm(pesquisa.getCriadoEm())
                    .finalizadoEm(pesquisa.getFinalizadoEm())
                    .prazo(pesquisa.getPrazo())
                    .ativo(pesquisa.getAtivo())
                    .totalUsuarios((long) usuariosAssociados.size())
                    .respondidos(usuariosAssociados.stream().filter(u -> Boolean.TRUE.equals(u.isRespondido())).count())
                    .build();

            responses.add(response);
        }

        responses.sort(Comparator
                .comparing(PesquisaResponse::getAtivo, Comparator.reverseOrder())
                .thenComparing(PesquisaResponse::getNome, String.CASE_INSENSITIVE_ORDER)
        );

        logger.info("Retornando {} pesquisas ordenadas", responses.size());
        return responses;
    }

    public PesquisaResponse getPesquisa(Long idPesquisa){
        logger.info("Buscando pesquisa pelo ID {}", idPesquisa);

        Pesquisa pesquisa = pesquisaRepository.getReferenceById(idPesquisa);
        if(pesquisa == null){
            logger.error("Pesquisa com ID {} não encontrada", idPesquisa);
            throw new EntityNotFoundException("Não existe uma pesquisa com esse id");
        }

        List<PesquisaColaborador> usuariosAssociados = pesquisaColaboradorRepository.findByPesquisa(pesquisa);
        logger.debug("Pesquisa {} tem {} colaboradores associados", pesquisa.getNome(), usuariosAssociados.size());

        return PesquisaResponse.builder()
                .idPesquisa(pesquisa.getId())
                .nome(pesquisa.getNome())
                .criadoEm(pesquisa.getCriadoEm())
                .finalizadoEm(pesquisa.getFinalizadoEm())
                .prazo(pesquisa.getPrazo())
                .ativo(pesquisa.getAtivo())
                .totalUsuarios((long) usuariosAssociados.size())
                .respondidos(usuariosAssociados.stream().filter(u -> Boolean.TRUE.equals(u.isRespondido())).count())
                .mensagem("Pesquisa retornada com sucesso")
                .build();
    }

    public PesquisaResponse finalizarPesquisa(Long idPesquisa){
        logger.info("Finalizando pesquisa com ID {}", idPesquisa);

        Pesquisa pesquisa = pesquisaRepository.getReferenceById(idPesquisa);

        if(pesquisa == null){
            logger.error("Pesquisa com ID {} não encontrada", idPesquisa);
            throw new EntityNotFoundException("Não existe uma pesquisa com esse id");
        }

        if(!pesquisa.getAtivo()){
            logger.warn("Tentativa de finalizar pesquisa já inativa: {}", pesquisa.getNome());
            throw new IllegalArgumentException("Essa proposta já está finalizada");
        }

        pesquisa.setFinalizadoEm(LocalDateTime.now());
        pesquisa.setAtivo(false);
        pesquisaRepository.save(pesquisa);
        logger.info("Pesquisa {} finalizada com sucesso", pesquisa.getNome());

        return PesquisaResponse.builder()
                .nome(pesquisa.getNome())
                .criadoEm(pesquisa.getCriadoEm())
                .finalizadoEm(pesquisa.getFinalizadoEm())
                .ativo(pesquisa.getAtivo())
                .mensagem("Pesquisa finalizada com sucesso")
                .build();
    }
}
