package br.com.aeris.aeris_search_config.service;

import br.com.aeris.aeris_search_config.dto.*;
import br.com.aeris.aeris_search_config.model.*;
import br.com.aeris.aeris_search_config.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
public class PerguntaService {
    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private OpcoesRepository opcoesRepository;

    @Autowired
    private TipoPerguntaRepository tipoPerguntaRepository;

    @Autowired
    private PesquisaColaboradorRepository pesquisaColaboradorRepository;

    private static final Logger logger = LoggerFactory.getLogger(PerguntaService.class);

    @Transactional
    public PerguntaResponse adicionarPergunta(PerguntaRequest request) {
        logger.info("Adicionando nova pergunta para a pesquisa ID {}", request.getPesquisaId());

        Pesquisa pesquisa = pesquisaRepository.findById(request.getPesquisaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pesquisa não encontrada com ID: " + request.getPesquisaId()
                ));

        Pergunta pergunta = new Pergunta();
        pergunta.setPergunta(request.getPergunta());
        pergunta.setAdjetivo(request.getAdjetivo());
        pergunta.setPesquisa(pesquisa);
        pergunta.setVisible(true);

        Pergunta perguntaSalva = perguntaRepository.save(pergunta);
        logger.debug("Pergunta '{}' salva com ID {}", perguntaSalva.getPergunta(), perguntaSalva.getId());

        TipoPergunta tipoPergunta = new TipoPergunta();
        tipoPergunta.setDescricao(request.getTipoPergunta());
        tipoPergunta.setPergunta(perguntaSalva);

        TipoPergunta tipoPerguntaSalva = tipoPerguntaRepository.save(tipoPergunta);
        perguntaSalva.setTipoPergunta(tipoPerguntaSalva);
        perguntaSalva = perguntaRepository.save(perguntaSalva);

        if ("opcoes".equalsIgnoreCase(request.getTipoPergunta()) &&
                request.getOpcoes() != null && !request.getOpcoes().isEmpty()) {

            List<Opcoes> opcoesEntidades = request.getOpcoes().stream()
                    .map(req -> {
                        Opcoes o = new Opcoes();
                        o.setDescricao(req.getDescricao());
                        o.setTipoPergunta(tipoPerguntaSalva);
                        return o;
                    })
                    .toList();

            opcoesRepository.saveAll(opcoesEntidades);
            logger.debug("Foram adicionadas {} opções para a pergunta ID {}", opcoesEntidades.size(), perguntaSalva.getId());
        }

        logger.info("Pergunta '{}' adicionada com sucesso (ID: {})", perguntaSalva.getPergunta(), perguntaSalva.getId());

        return PerguntaResponse.builder()
                .mensagem("Pergunta adicionada com sucesso")
                .id(perguntaSalva.getId())
                .pergunta(perguntaSalva.getPergunta())
                .adjetivo(perguntaSalva.getAdjetivo())
                .build();
    }

    @Transactional
    public PerguntaResponse atualizarPergunta(Long idPergunta, PerguntaRequest request) {
        logger.info("Atualizando pergunta ID {}", idPergunta);

        Pergunta pergunta = perguntaRepository.findById(idPergunta)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pergunta não encontrada com ID: " + idPergunta
                ));

        pergunta.setPergunta(request.getPergunta());
        pergunta.setAdjetivo(request.getAdjetivo());

        if (request.getPesquisaId() != null &&
                (pergunta.getPesquisa() == null ||
                        !pergunta.getPesquisa().getId().equals(request.getPesquisaId()))) {

            Pesquisa pesquisa = pesquisaRepository.findById(request.getPesquisaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Pesquisa não encontrada com ID: " + request.getPesquisaId()
                    ));
            pergunta.setPesquisa(pesquisa);
            logger.debug("Pesquisa associada atualizada para ID {}", pesquisa.getId());
        }

        TipoPergunta tipoPergunta = pergunta.getTipoPergunta();
        if (tipoPergunta == null) {
            tipoPergunta = new TipoPergunta();
            tipoPergunta.setPergunta(pergunta);
        }

        tipoPergunta.setDescricao(request.getTipoPergunta());
        tipoPergunta = tipoPerguntaRepository.save(tipoPergunta);

        pergunta.setTipoPergunta(tipoPergunta);
        perguntaRepository.save(pergunta);
        logger.debug("Tipo de pergunta atualizado para '{}'", tipoPergunta.getDescricao());

        if ("opcoes".equalsIgnoreCase(request.getTipoPergunta())) {
            List<Opcoes> opcoesAntigas = opcoesRepository.findByTipoPergunta(tipoPergunta);
            if (!opcoesAntigas.isEmpty()) {
                opcoesRepository.deleteAll(opcoesAntigas);
                logger.debug("Removidas {} opções antigas da pergunta ID {}", opcoesAntigas.size(), idPergunta);
            }

            if (request.getOpcoes() != null && !request.getOpcoes().isEmpty()) {
                TipoPergunta finalTipoPergunta = tipoPergunta;
                List<Opcoes> novasOpcoes = request.getOpcoes().stream()
                        .map(req -> {
                            Opcoes o = new Opcoes();
                            o.setDescricao(req.getDescricao());
                            o.setTipoPergunta(finalTipoPergunta);
                            return o;
                        })
                        .toList();

                opcoesRepository.saveAll(novasOpcoes);
                logger.debug("Foram adicionadas {} novas opções à pergunta ID {}", novasOpcoes.size(), idPergunta);
            }
        }

        logger.info("Pergunta ID {} atualizada com sucesso", idPergunta);

        return PerguntaResponse.builder()
                .mensagem("Pergunta atualizada com sucesso")
                .id(pergunta.getId())
                .pergunta(pergunta.getPergunta())
                .adjetivo(pergunta.getAdjetivo())
                .build();
    }

    public List<PerguntaResponse> getAllPerguntas(Long pesquisa) {
        logger.info("Buscando todas as perguntas da pesquisa ID {}", pesquisa);

        List<Pergunta> perguntas = perguntaRepository.findByPesquisa(pesquisaRepository.getReferenceById(pesquisa))
                .stream()
                .filter(Pergunta::isVisible)
                .toList();

        if (perguntas == null || perguntas.isEmpty()) {
            logger.warn("Nenhuma pergunta encontrada para a pesquisa ID {}", pesquisa);
            throw new EntityNotFoundException("Essa pesquisa ainda não possui perguntas");
        }

        List<PerguntaResponse> responses = new ArrayList<>();

        for (Pergunta pergunta : perguntas) {
            TipoPergunta tipoPergunta = tipoPerguntaRepository.findByPergunta(pergunta);
            List<Opcoes> opcoes = opcoesRepository.findByTipoPergunta(tipoPergunta);

            List<OpcoesResponse> opcoesResponse = opcoes.stream()
                    .map(opcao -> OpcoesResponse.builder()
                            .id(opcao.getId())
                            .descricao(opcao.getDescricao())
                            .build())
                    .toList();

            TipoPerguntaResponse tipoPerguntaResponse = TipoPerguntaResponse.builder()
                    .id(tipoPergunta.getId())
                    .descricao(tipoPergunta.getDescricao())
                    .opcoes(opcoesResponse)
                    .build();

            responses.add(PerguntaResponse.builder()
                    .id(pergunta.getId())
                    .pergunta(pergunta.getPergunta())
                    .adjetivo(pergunta.getAdjetivo())
                    .tipoPergunta(tipoPerguntaResponse)
                    .build());

            logger.debug("Pergunta '{}' carregada ({} opções)", pergunta.getPergunta(), opcoes.size());
        }

        responses.sort(Comparator.comparing(PerguntaResponse::getPergunta, String.CASE_INSENSITIVE_ORDER));
        logger.info("Total de perguntas retornadas: {}", responses.size());

        return responses;
    }

    public PerguntaResponse deletarPergunta(Long idPergunta) {
        logger.info("Deletando (ocultando) pergunta ID {}", idPergunta);

        Pergunta pergunta = perguntaRepository.findById(idPergunta)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pergunta não encontrada com ID: " + idPergunta
                ));

        pergunta.setVisible(false);
        perguntaRepository.save(pergunta);

        logger.info("Pergunta '{}' (ID: {}) marcada como invisível", pergunta.getPergunta(), pergunta.getId());

        return PerguntaResponse.builder()
                .mensagem("Pergunta deletada com sucesso")
                .id(pergunta.getId())
                .pergunta(pergunta.getPergunta())
                .adjetivo(pergunta.getAdjetivo())
                .build();
    }


}
