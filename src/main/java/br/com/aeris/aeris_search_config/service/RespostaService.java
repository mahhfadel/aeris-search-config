package br.com.aeris.aeris_search_config.service;

import br.com.aeris.aeris_search_config.dto.RespostaItemRequest;
import br.com.aeris.aeris_search_config.dto.RespostaRequest;
import br.com.aeris.aeris_search_config.dto.RespostaResponse;
import br.com.aeris.aeris_search_config.model.*;
import br.com.aeris.aeris_search_config.repository.*;
import br.com.aeris.aeris_search_config.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RespostaService {

    @Autowired
    private RespostaRepository respostaRepository;

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private OpcoesRepository opcoesRepository;

    @Autowired
    private PesquisaColaboradorRepository pesquisaColaboradorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public RespostaResponse salvarRespostas(RespostaRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(jwtUtil.extractEmail(request.getTokenUser()));

        // Validar se já não respondeu
        if (respostaRepository.existsByPesquisaIdAndUsuarioId(
                request.getPesquisaId(), usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Usuário já respondeu esta pesquisa");
        }

        List<Resposta> respostas = new ArrayList<>();

        for (RespostaItemRequest item : request.getRespostas()) {
            // Validar se a pergunta existe e pertence à pesquisa
            Pergunta pergunta = perguntaRepository.findById(item.getPerguntaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Pergunta não encontrada: " + item.getPerguntaId()));

            if (!pergunta.getPesquisa().getId().equals(request.getPesquisaId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Pergunta não pertence à pesquisa informada");
            }

            Resposta resposta = new Resposta();
            resposta.setPerguntaId(item.getPerguntaId());
            resposta.setPesquisaId(request.getPesquisaId());
            resposta.setUsuarioId(usuario.getId());
            resposta.setTipoPergunta(item.getTipoPergunta());

            // Processar e validar cada tipo de resposta
            switch (item.getTipoPergunta().toLowerCase()) {
                case "descritiva":
                    validarRespostaDescritiva(item);
                    resposta.setRespostaDescritiva(item.getRespostaDescritiva());
                    break;

                case "escala":
                    validarRespostaEscala(item);
                    resposta.setRespostaEscala(item.getRespostaEscala());
                    break;

                case "opcoes":
                    validarRespostaOpcoes(item, pergunta);
                    resposta.setRespostaOpcoes(item.getRespostaOpcoes());
                    break;

                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Tipo de pergunta inválido: " + item.getTipoPergunta());
            }

            respostas.add(resposta);
        }

        respostaRepository.saveAll(respostas);

        PesquisaColaborador pesquisaColaborador = pesquisaColaboradorRepository.findByPesquisaIdAndUsuarioId(request.getPesquisaId(), usuario.getId());
        pesquisaColaborador.setRespondido(true);;
        pesquisaColaboradorRepository.save(pesquisaColaborador);

        return RespostaResponse.builder()
                .pesquisaId(request.getPesquisaId())
                .usuarioId(usuario.getId())
                .totalRespostas(respostas.size())
                .submissaoEm(LocalDateTime.now())
                .status("CONCLUIDO")
                .build();
    }

    private void validarRespostaDescritiva(RespostaItemRequest item) {
        if (item.getRespostaDescritiva() == null || item.getRespostaDescritiva().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Resposta descritiva não pode ser nula ou vazia");
        }

        if (item.getRespostaDescritiva().length() < 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Resposta descritiva deve ter pelo menos 10 caracteres");
        }
    }

    private void validarRespostaEscala(RespostaItemRequest item) {
        if (item.getRespostaEscala() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Resposta de escala não pode ser nula");
        }
    }

    private void validarRespostaOpcoes(RespostaItemRequest item, Pergunta pergunta) {
        if (item.getRespostaOpcoes() == null || item.getRespostaOpcoes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Selecione pelo menos uma opção");
        }

        if (item.getRespostaOpcoes().size() > 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Você pode selecionar no máximo 4 opções");
        }
    }

    public List<Resposta> buscarRespostasPorPesquisaEUsuario(Long pesquisaId, Long usuarioId) {
        return respostaRepository.findByPesquisaIdAndUsuarioId(pesquisaId, usuarioId);
    }
}
