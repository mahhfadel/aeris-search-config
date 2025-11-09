package br.com.aeris.aeris_search_config.service;

import br.com.aeris.aeris_search_config.dto.RespostaItemRequest;
import br.com.aeris.aeris_search_config.dto.RespostaRequest;
import br.com.aeris.aeris_search_config.dto.RespostaResponse;
import br.com.aeris.aeris_search_config.model.*;
import br.com.aeris.aeris_search_config.repository.*;
import br.com.aeris.aeris_search_config.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private static final Logger logger = LoggerFactory.getLogger(RespostaService.class);

    @Transactional
    public RespostaResponse salvarRespostas(RespostaRequest request) {
        logger.info("Iniciando processo de salvamento de respostas para pesquisa ID: {}", request.getPesquisaId());

        Usuario usuario = usuarioRepository.findByEmail(jwtUtil.extractEmail(request.getTokenUser()));
        logger.debug("Usuário autenticado: {} (ID: {})", usuario.getEmail(), usuario.getId());

        // Validar se já não respondeu
        if (respostaRepository.existsByPesquisaIdAndUsuarioId(request.getPesquisaId(), usuario.getId())) {
            logger.warn("Usuário {} tentou responder novamente a pesquisa ID {}", usuario.getEmail(), request.getPesquisaId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já respondeu esta pesquisa");
        }

        List<Resposta> respostas = new ArrayList<>();

        for (RespostaItemRequest item : request.getRespostas()) {
            logger.debug("Processando resposta para pergunta ID: {} (tipo: {})", item.getPerguntaId(), item.getTipoPergunta());

            Pergunta pergunta = perguntaRepository.findById(item.getPerguntaId())
                    .orElseThrow(() -> {
                        logger.error("Pergunta não encontrada: {}", item.getPerguntaId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Pergunta não encontrada: " + item.getPerguntaId());
                    });

            if (!pergunta.getPesquisa().getId().equals(request.getPesquisaId())) {
                logger.error("Pergunta ID {} não pertence à pesquisa ID {}", item.getPerguntaId(), request.getPesquisaId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Pergunta não pertence à pesquisa informada");
            }

            Resposta resposta = new Resposta();
            resposta.setPerguntaId(item.getPerguntaId());
            resposta.setPesquisaId(request.getPesquisaId());
            resposta.setUsuarioId(usuario.getId());
            resposta.setTipoPergunta(item.getTipoPergunta());

            try {
                switch (item.getTipoPergunta().toLowerCase()) {
                    case "descritiva":
                        validarRespostaDescritiva(item);
                        resposta.setRespostaDescritiva(item.getRespostaDescritiva());
                        logger.trace("Resposta descritiva registrada: {}", item.getRespostaDescritiva());
                        break;

                    case "escala":
                        validarRespostaEscala(item);
                        resposta.setRespostaEscala(item.getRespostaEscala());
                        logger.trace("Resposta escala registrada: {}", item.getRespostaEscala());
                        break;

                    case "opcoes":
                        validarRespostaOpcoes(item, pergunta);
                        resposta.setRespostaOpcoes(item.getRespostaOpcoes());
                        logger.trace("Resposta de opções registrada: {}", item.getRespostaOpcoes());
                        break;

                    default:
                        logger.error("Tipo de pergunta inválido recebido: {}", item.getTipoPergunta());
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Tipo de pergunta inválido: " + item.getTipoPergunta());
                }
            } catch (ResponseStatusException e) {
                logger.warn("Falha ao validar resposta para pergunta ID {}: {}", item.getPerguntaId(), e.getReason());
                throw e;
            }

            respostas.add(resposta);
        }

        respostaRepository.saveAll(respostas);
        logger.info("{} respostas salvas com sucesso para o usuário {}", respostas.size(), usuario.getEmail());

        PesquisaColaborador pesquisaColaborador = pesquisaColaboradorRepository
                .findByPesquisaIdAndUsuarioId(request.getPesquisaId(), usuario.getId());

        pesquisaColaborador.setRespondido(true);
        pesquisaColaboradorRepository.save(pesquisaColaborador);
        logger.debug("Marcado como respondido em pesquisa_colaborador (pesquisa: {}, usuário: {})",
                request.getPesquisaId(), usuario.getId());

        RespostaResponse response = RespostaResponse.builder()
                .pesquisaId(request.getPesquisaId())
                .usuarioId(usuario.getId())
                .totalRespostas(respostas.size())
                .submissaoEm(LocalDateTime.now())
                .status("CONCLUIDO")
                .build();

        logger.info("Respostas finalizadas e resposta enviada: {}", response);
        return response;
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
