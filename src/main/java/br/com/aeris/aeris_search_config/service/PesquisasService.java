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

import jakarta.persistence.EntityNotFoundException;
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

    public PesquisaResponse createPesquisa(String token){
        Usuario usuario = usuarioRepository.findByEmail(jwtUtil.extractEmail(token));

        if (!usuario.getAtivo()) {
            throw new IllegalArgumentException("Usuário inativo");
        }

        if (!Objects.equals(usuario.getTipo(), "adm")) {
            throw new IllegalArgumentException("Usuário não tem acesso");
        }

        List<Pesquisa> quantPesquisasEmpresa = pesquisaRepository.findByEmpresa(usuario.getEmpresa());

        Pesquisa pesquisa = new Pesquisa();
        pesquisa.setCriadoEm(LocalDate.now());
        pesquisa.setPrazo(LocalDate.now().plus(Period.ofDays(30)));
        pesquisa.setNome(String.format("Pesquisa #%03d", (long) quantPesquisasEmpresa.size()));
        pesquisa.setUsuarioId(usuario.getId());
        pesquisa.setAtivo(true);
        pesquisa.setEmpresa(usuario.getEmpresa());

        Pesquisa pesquisaCriada = pesquisaRepository.save(pesquisa);

        return PesquisaResponse.builder()
                .idPesquisa(pesquisaCriada.getId())
                .nome(pesquisaCriada.getNome())
                .mensagem("Pesquisa criada com sucesso")
                .build();

    }

    public List<PesquisaResponse> getAllPesquisas(Long empresa){
        List<Pesquisa> pesquisas = pesquisaRepository.findByEmpresa(empresaRepository.getReferenceById(empresa));

        if(pesquisas == null){
            throw new EntityNotFoundException("Essa empresa ainda não possui pesquisas");
        }

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
                    .respondidos(usuariosAssociados.stream().filter(u -> Objects.equals(u.isRespondido(), true)).count())
                    .build();

            responses.add(response);
        }

        responses.sort(Comparator
                .comparing(PesquisaResponse::getAtivo, Comparator.reverseOrder())
                .thenComparing(PesquisaResponse::getNome, String.CASE_INSENSITIVE_ORDER)
        );


        return responses;
    }

    public PesquisaResponse getPesquisa(Long idPesquisa){
        Pesquisa pesquisa = pesquisaRepository.getReferenceById(idPesquisa);

        if(pesquisa == null){
            throw new EntityNotFoundException("Não existe uma pesquisa com esse id");
        }

        List<PesquisaColaborador> usuariosAssociados = pesquisaColaboradorRepository.findByPesquisa(pesquisa);

        return PesquisaResponse.builder()
                .idPesquisa(pesquisa.getId())
                .nome(pesquisa.getNome())
                .criadoEm(pesquisa.getCriadoEm())
                .finalizadoEm(pesquisa.getFinalizadoEm())
                .prazo(pesquisa.getPrazo())
                .ativo(pesquisa.getAtivo())
                .totalUsuarios((long) usuariosAssociados.size())
                .respondidos(usuariosAssociados.stream().filter(u -> Objects.equals(u.isRespondido(), true)).count())
                .mensagem("Pesquisa retornada com sucesso")
                .build();
    }

    public PesquisaResponse finalizarPesquisa(Long idPesquisa){
        Pesquisa pesquisa = pesquisaRepository.getReferenceById(idPesquisa);

        if(pesquisa == null){
            throw new EntityNotFoundException("Não existe uma pesquisa com esse id");
        }

        if(!pesquisa.getAtivo()){
            throw new IllegalArgumentException("Essa proposta ja está finalizada");
        }

        pesquisa.setFinalizadoEm(LocalDateTime.now());
        pesquisa.setAtivo(false);
        pesquisaRepository.save(pesquisa);

        return PesquisaResponse.builder()
                .nome(pesquisa.getNome())
                .criadoEm(pesquisa.getCriadoEm())
                .finalizadoEm(pesquisa.getFinalizadoEm())
                .ativo(pesquisa.getAtivo())
                .mensagem("Pesquisa finalizada com sucesso")
                .build();
    }
}
