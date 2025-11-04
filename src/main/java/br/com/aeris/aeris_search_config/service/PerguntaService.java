package br.com.aeris.aeris_search_config.service;

import br.com.aeris.aeris_search_config.dto.*;
import br.com.aeris.aeris_search_config.model.*;
import br.com.aeris.aeris_search_config.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


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


    @Transactional
    public PerguntaResponse adicionarPergunta(PerguntaRequest request) {
        Pesquisa pesquisa = pesquisaRepository.findById(request.getPesquisaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pesquisa não encontrada com ID: " + request.getPesquisaId()
                ));

        Pergunta pergunta = new Pergunta();
        pergunta.setPergunta(request.getPergunta());
        pergunta.setAdjetivo(request.getAdjetivo());
        pergunta.setPesquisa(pesquisa);

        Pergunta perguntaSalva = perguntaRepository.save(pergunta);

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
        }

        return PerguntaResponse.builder()
                .mensagem("Pergunta adicionada com sucesso")
                .id(perguntaSalva.getId())
                .pergunta(perguntaSalva.getPergunta())
                .adjetivo(perguntaSalva.getAdjetivo())
                .build();
    }


    @Transactional
    public PerguntaResponse atualizarPergunta(Long idPergunta, PerguntaRequest request) {
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

        if ("opcoes".equalsIgnoreCase(request.getTipoPergunta())) {
            List<Opcoes> opcoesAntigas = opcoesRepository.findByTipoPergunta(tipoPergunta);
            if (!opcoesAntigas.isEmpty()) {
                opcoesRepository.deleteAll(opcoesAntigas);
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
            }
        }

        return PerguntaResponse.builder()
                .mensagem("Pergunta atualizada com sucesso")
                .id(pergunta.getId())
                .pergunta(pergunta.getPergunta())
                .adjetivo(pergunta.getAdjetivo())
                .build();
    }

    public List<PerguntaResponse> getAllPerguntas(Long pesquisa){
        List<Pergunta> perguntas = perguntaRepository.findByPesquisa(pesquisaRepository.getReferenceById(pesquisa));

        if(perguntas == null){
            throw new EntityNotFoundException("Essa pesquisa ainda não possui perguntas");
        }

        List<PerguntaResponse> responses = new ArrayList<>();

        for(Pergunta pergunta: perguntas){
            TipoPergunta tipoPergunta = tipoPerguntaRepository.findByPergunta(pergunta);

            List<Opcoes> opcoes = opcoesRepository.findByTipoPergunta(tipoPergunta);

            List<OpcoesResponse> opcoesResponse = new ArrayList<>();

            for (Opcoes opcao: opcoes){
                OpcoesResponse opcaoResponse = OpcoesResponse.builder()
                        .id(opcao.getId())
                        .descricao(opcao.getDescricao())
                        .build();

                opcoesResponse.add(opcaoResponse);
            }

            TipoPerguntaResponse tipoPerguntaResponse = TipoPerguntaResponse.builder()
                    .id(tipoPergunta.getId())
                    .descricao(tipoPergunta.getDescricao())
                    .opcoes(opcoesResponse)
                    .build();

            PerguntaResponse response = PerguntaResponse.builder()
                    .id(pergunta.getId())
                    .pergunta(pergunta.getPergunta())
                    .adjetivo(pergunta.getAdjetivo())
                    .tipoPergunta(tipoPerguntaResponse)
                    .build();

            responses.add(response);
        }

        responses.sort(Comparator
                .comparing(PerguntaResponse::getPergunta, String.CASE_INSENSITIVE_ORDER)
        );

        return responses;
    }

}
