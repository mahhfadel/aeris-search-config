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

    public ColaboradorService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Transactional
    public ColaboradorResponse adicionarColaborador(List<Long> colaboradores, Long pesquisaId){
        Pesquisa pesquisa = pesquisaRepository.getReferenceById(pesquisaId);

        for(Long colaborador: colaboradores){
            Usuario usuario = usuarioRepository.getReferenceById(colaborador);

            String chave = gerarChaveAleatoria();

            PesquisaColaborador novoColaboradorPesquisa = new PesquisaColaborador();
            novoColaboradorPesquisa.setPesquisa(pesquisa);
            novoColaboradorPesquisa.setUsuario(usuario);
            novoColaboradorPesquisa.setRespondido(false);
            novoColaboradorPesquisa.setToken(passwordEncoder.encode(chave));

            pesquisaColaboradorRepository.save(novoColaboradorPesquisa);

            emailService.enviarEmailNovaPesquisa(
                    usuario.getEmail(),
                    usuario.getNome(),
                    usuario.getEmpresa().getNome(),
                    chave
            );
        }

        return ColaboradorResponse.builder()
                .mensagem(String.format("%d novos colaboradores adicionado com sucesso à pesquisa %s", colaboradores.size(), pesquisa.getNome()))
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

    public List<ColaboradorResponse> getAllColaboradoresUsers(Long empresa, Long pesquisa){
        List<PesquisaColaborador> colaboradores = pesquisaColaboradorRepository.findByPesquisa(pesquisaRepository.getReferenceById(pesquisa));

        List<ColaboradorResponse> responses = new ArrayList<>();

        for(PesquisaColaborador colaborador: colaboradores){
            Usuario usuario = usuarioRepository.getReferenceById(colaborador.getUsuario().getId());
            DadosPessoais dadosPessoais = dadosPessoaisRepository.findByUsuario(usuario);
            if(dadosPessoais ==null){
                dadosPessoais = new DadosPessoais();
            }
            List<PesquisaColaborador> pesquisasColaborador = pesquisaColaboradorRepository.findByUsuario(usuario);

            ColaboradorResponse response = ColaboradorResponse.builder()
                    .id(colaborador.getId())
                    .nome(usuario.getNome().concat(" ").concat(usuario.getSobrenome()))
                    .email(usuario.getEmail())
                    .genero(dadosPessoais.getGenero())
                    .setor(dadosPessoais.getSetor())
                    .cargo(dadosPessoais.getCargo())
                    .tempoDeCasa(formatarPeriodo(dadosPessoais.getContratadoEm()))
                    .respondidos(pesquisasColaborador.stream().filter(u ->Objects.equals(u.isRespondido(), true) ).count())
                    .total((long) pesquisasColaborador.size())
                    .build();

            responses.add(response);
        }

        responses.sort(Comparator.comparing(ColaboradorResponse::getNome, String.CASE_INSENSITIVE_ORDER));

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
