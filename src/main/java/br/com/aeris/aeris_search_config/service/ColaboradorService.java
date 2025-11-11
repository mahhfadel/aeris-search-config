package br.com.aeris.aeris_search_config.service;

import br.com.aeris.aeris_search_config.dto.ColaboradorResponse;
import br.com.aeris.aeris_search_config.model.DadosPessoais;
import br.com.aeris.aeris_search_config.model.Pesquisa;
import br.com.aeris.aeris_search_config.model.PesquisaColaborador;
import br.com.aeris.aeris_search_config.model.Usuario;
import br.com.aeris.aeris_search_config.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.SecureRandom;

import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ColaboradorService {

    @Autowired
    private PesquisaColaboradorRepository pesquisaColaboradorRepository;

    @Autowired
    private PesquisaRepository pesquisaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DadosPessoaisRepository dadosPessoaisRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(ColaboradorService.class);

    public ColaboradorService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Transactional
    public ColaboradorResponse adicionarColaborador(List<Long> colaboradores, Long pesquisaId) {
        logger.info("Iniciando adição de {} colaboradores à pesquisa com ID {}", colaboradores.size(), pesquisaId);

        Pesquisa pesquisa = pesquisaRepository.getReferenceById(pesquisaId);

        for (Long colaboradorId : colaboradores) {
            try {
                Usuario usuario = usuarioRepository.getReferenceById(colaboradorId);
                String chave = gerarChaveAleatoria();

                PesquisaColaborador novoColaboradorPesquisa = new PesquisaColaborador();
                novoColaboradorPesquisa.setPesquisa(pesquisa);
                novoColaboradorPesquisa.setUsuario(usuario);
                novoColaboradorPesquisa.setRespondido(false);
                novoColaboradorPesquisa.setToken(passwordEncoder.encode(chave));

                pesquisaColaboradorRepository.save(novoColaboradorPesquisa);

                logger.debug("Colaborador {} adicionado à pesquisa {}", usuario.getEmail(), pesquisa.getNome());

                try {
                    emailService.enviarEmailNovaPesquisa(
                            usuario.getEmail(),
                            usuario.getNome(),
                            usuario.getEmpresa().getNome(),
                            chave
                    );

                    logger.info("Email enviado para {}", usuario.getEmail());
                } catch (Exception e) {
                    logger.error("Erro ao agendar envio de email", e);
                }

            } catch (Exception e) {
                logger.error("Erro ao adicionar colaborador com ID {} à pesquisa {}: {}", colaboradorId, pesquisaId, e.getMessage(), e);
            }
        }

        logger.info("{} colaboradores adicionados com sucesso à pesquisa '{}'", colaboradores.size(), pesquisa.getNome());

        return ColaboradorResponse.builder()
                .mensagem(String.format("%d novos colaboradores adicionados com sucesso à pesquisa %s", colaboradores.size(), pesquisa.getNome()))
                .build();
    }


    public static String gerarChaveAleatoria() {
        SecureRandom RANDOM = new SecureRandom();

        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        int length = 8 + RANDOM.nextInt(8);
        StringBuilder chave = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            chave.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        return chave.toString();
    }

    public List<ColaboradorResponse> getAllColaboradoresUsers(Long empresa, Long pesquisaId) {
        logger.info("Buscando colaboradores da empresa {} para a pesquisa {}", empresa, pesquisaId);

        List<PesquisaColaborador> colaboradores = pesquisaColaboradorRepository.findByPesquisa(
                pesquisaRepository.getReferenceById(pesquisaId)
        );

        logger.debug("Foram encontrados {} registros de colaboradores para a pesquisa {}", colaboradores.size(), pesquisaId);

        List<ColaboradorResponse> responses = new ArrayList<>();

        for (PesquisaColaborador colaborador : colaboradores) {
            try {
                Usuario usuario = usuarioRepository.getReferenceById(colaborador.getUsuario().getId());
                DadosPessoais dadosPessoais = dadosPessoaisRepository.findByUsuario(usuario);

                if (dadosPessoais == null) {
                    logger.warn("Usuário {} ({}) não possui dados pessoais cadastrados.", usuario.getEmail(), usuario.getId());
                    dadosPessoais = new DadosPessoais(); // Evita NullPointerException
                }

                List<PesquisaColaborador> pesquisasColaborador = pesquisaColaboradorRepository.findByUsuario(usuario);

                long respondidos = pesquisasColaborador.stream()
                        .filter(PesquisaColaborador::isRespondido)
                        .count();

                ColaboradorResponse response = ColaboradorResponse.builder()
                        .id(colaborador.getId())
                        .nome(usuario.getNome().concat(" ").concat(usuario.getSobrenome()))
                        .email(usuario.getEmail())
                        .genero(dadosPessoais.getGenero())
                        .setor(dadosPessoais.getSetor())
                        .cargo(dadosPessoais.getCargo())
                        .tempoDeCasa(formatarPeriodo(dadosPessoais.getContratadoEm()))
                        .respondidos(respondidos)
                        .total((long) pesquisasColaborador.size())
                        .build();

                responses.add(response);

                logger.debug("Colaborador processado: {} ({})", usuario.getEmail(), usuario.getId());

            } catch (Exception e) {
                logger.error("Erro ao processar colaborador ID {}: {}", colaborador.getUsuario().getId(), e.getMessage(), e);
            }
        }

        responses.sort(Comparator.comparing(ColaboradorResponse::getNome, String.CASE_INSENSITIVE_ORDER));
        logger.info("Total de colaboradores retornados: {}", responses.size());

        return responses;
    }


    public static String formatarPeriodo(LocalDate data) {
        if (data == null) {
            return "Periodo não informada";
        }

        LocalDate agora = LocalDate.now();

        if (data.isAfter(ChronoLocalDate.from(agora))) {
            return "-";
        }

        Period periodo = Period.between(
                data,
                agora
        );

        int anos = periodo.getYears();
        int meses = periodo.getMonths();

        StringBuilder resultado = new StringBuilder();

        if (anos > 0) {
            resultado.append(anos).append(anos == 1 ? " ano" : " anos");
        }

        if (resultado.length() > 0) {
            resultado.append(" e ");
        }

        if(meses > 0) {
            resultado.append(meses).append(meses == 1 ? " mês" : " meses");
        } else {
            resultado.append("1 mês");
        }

        return resultado.toString();
    }
}
