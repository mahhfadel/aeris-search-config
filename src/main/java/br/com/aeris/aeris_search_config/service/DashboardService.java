package br.com.aeris.aeris_search_config.service;

import br.com.aeris.aeris_search_config.dto.DashboardResponse;
import br.com.aeris.aeris_search_config.dto.DataDashboardResponse;
import br.com.aeris.aeris_search_config.model.*;
import br.com.aeris.aeris_search_config.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Autowired
    private final PesquisaRepository pesquisaRepository;

    @Autowired
    private final DadosPessoaisRepository dadosPessoaisRepository;

    @Autowired
    private final PesquisaColaboradorRepository pesquisaColaboradorRepository;

    @Autowired
    private final RespostaRepository respostaRepository;

    @Autowired
    private final PerguntaRepository perguntaRepository;

    public List<DashboardResponse> getDashboardData() {
        List<DashboardResponse> response = new ArrayList<>();

        // DADOS USUARIOS ==============================================================================================
        // Quantidade de colaboradores
        List<DataDashboardResponse> quantColaboradores = new ArrayList<>();
        quantColaboradores.add(DataDashboardResponse.builder()
                .name("Quantidade colaboradores")
                .value(usuarioRepository.findAll().stream()
                        .filter(usuario -> usuario.getAtivo() == true &&
                                Objects.equals(usuario.getTipo(), "colaborador"))
                        .count())
                .fill("#AC4A00")
                .build());

        response.add(DashboardResponse.builder()
                    .titulo("Quantidade de colaboradores ativos")
                    .descricao("Soma dos colaboradores ativos na plataforma")
                    .values(quantColaboradores)
                    .build());

        //Quant ja responderam senso
        List<DataDashboardResponse> quantRespondeuSenso = new ArrayList<>();
        quantRespondeuSenso.add(DataDashboardResponse.builder()
                .name("Responderam o senso")
                .value((long) dadosPessoaisRepository.findAll().size())
                .fill("#AC4A00")
                .build());

        response.add(DashboardResponse.builder()
                .titulo("Quantidade de colaboradores responderam o senso")
                .descricao("Soma dos colaboradores no total que ja responderam o senso inicial")
                .values(quantRespondeuSenso)
                .build());

        // Média de idade por faixas etárias
        List<DataDashboardResponse> mediaIdade = new ArrayList<>();
        List<DadosPessoais> todosDados = dadosPessoaisRepository.findAll();

        if (!todosDados.isEmpty()) {
            Map<String, Long> faixasEtarias = new LinkedHashMap<>();
            faixasEtarias.put("Menor de 18", 0L);
            faixasEtarias.put("18 a 24", 0L);
            faixasEtarias.put("25 a 30", 0L);
            faixasEtarias.put("31 a 40", 0L);
            faixasEtarias.put("41 a 50", 0L);
            faixasEtarias.put("51 a 65", 0L);
            faixasEtarias.put("65+", 0L);

            for (DadosPessoais dados : todosDados) {
                int idade = Period.between(dados.getDataNascimento(), LocalDate.now()).getYears();

                if (idade < 18) {
                    faixasEtarias.put("Menor de 18", faixasEtarias.get("Menor de 18") + 1);
                } else if (idade <= 24) {
                    faixasEtarias.put("18 a 24", faixasEtarias.get("18 a 24") + 1);
                } else if (idade <= 30) {
                    faixasEtarias.put("25 a 30", faixasEtarias.get("25 a 30") + 1);
                } else if (idade <= 40) {
                    faixasEtarias.put("31 a 40", faixasEtarias.get("31 a 40") + 1);
                } else if (idade <= 50) {
                    faixasEtarias.put("41 a 50", faixasEtarias.get("41 a 50") + 1);
                } else if (idade <= 65) {
                    faixasEtarias.put("51 a 65", faixasEtarias.get("51 a 65") + 1);
                } else {
                    faixasEtarias.put("65+", faixasEtarias.get("65+") + 1);
                }
            }

            String[] coresIdade = {"#AC4A00", "#FBBC04", "#E86500", "#E88A00", "#B6601E", "#F7DC6F", "#BB8FCE"};
            int index = 0;

            for (Map.Entry<String, Long> entry : faixasEtarias.entrySet()) {
                mediaIdade.add(DataDashboardResponse.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .fill(coresIdade[index])
                        .build());
                index++;
            }
        }

        response.add(DashboardResponse.builder()
                .titulo("Distribuição por faixa etária")
                .descricao("Quantidade de colaboradores agrupados por faixa etária")
                .values(mediaIdade)
                .build());

    // Quantidade por gênero
        List<DataDashboardResponse> quantPorGenero = new ArrayList<>();
        Map<String, Long> generoMap = todosDados.stream()
                .collect(Collectors.groupingBy(DadosPessoais::getGenero, Collectors.counting()));

        String[] coresGenero = {"#AC4A00", "#FBBC04", "#E86500", "#E88A00", "#B6601E"};
        int indexGenero = 0;

        for (Map.Entry<String, Long> entry : generoMap.entrySet()) {
            quantPorGenero.add(DataDashboardResponse.builder()
                    .name(entry.getKey())
                    .value(entry.getValue())
                    .fill(coresGenero[indexGenero % coresGenero.length])
                    .build());
            indexGenero++;
        }

        response.add(DashboardResponse.builder()
                .titulo("Distribuição por gênero")
                .descricao("Quantidade de colaboradores agrupados por gênero")
                .values(quantPorGenero)
                .build());

    // Quantidade por setor
        List<DataDashboardResponse> quantPorSetor = new ArrayList<>();
        Map<String, Long> setorMap = todosDados.stream()
                .collect(Collectors.groupingBy(DadosPessoais::getSetor, Collectors.counting()));

        String[] coresSetor = {"#AC4A00", "#FBBC04", "#E86500", "#E88A00", "#B6601E", "#673AB7"};
        int indexSetor = 0;

        for (Map.Entry<String, Long> entry : setorMap.entrySet()) {
            quantPorSetor.add(DataDashboardResponse.builder()
                    .name(entry.getKey())
                    .value(entry.getValue())
                    .fill(coresSetor[indexSetor % coresSetor.length])
                    .build());
            indexSetor++;
        }

        response.add(DashboardResponse.builder()
                .titulo("Distribuição por setor")
                .descricao("Quantidade de colaboradores agrupados por setor de atuação")
                .values(quantPorSetor)
                .build());

    // Quantidade por cargo
        List<DataDashboardResponse> quantPorCargo = new ArrayList<>();
        Map<String, Long> cargoMap = todosDados.stream()
                .collect(Collectors.groupingBy(DadosPessoais::getCargo, Collectors.counting()));

        String[] coresCargo = {"#AC4A00", "#FBBC04", "#E86500", "#E88A00", "#B6601E", "#607D8B"};
        int indexCargo = 0;

        for (Map.Entry<String, Long> entry : cargoMap.entrySet()) {
            quantPorCargo.add(DataDashboardResponse.builder()
                    .name(entry.getKey())
                    .value(entry.getValue())
                    .fill(coresCargo[indexCargo % coresCargo.length])
                    .build());
            indexCargo++;
        }

        response.add(DashboardResponse.builder()
                .titulo("Distribuição por cargo")
                .descricao("Quantidade de colaboradores agrupados por cargo")
                .values(quantPorCargo)
                .build());

    // Quantidade por sexualidade
        List<DataDashboardResponse> quantPorSexualidade = new ArrayList<>();
        Map<String, Long> sexualidadeMap = todosDados.stream()
                .collect(Collectors.groupingBy(DadosPessoais::getSexualidade, Collectors.counting()));

        String[] coresSexualidade = {"#AC4A00", "#FBBC04", "#E86500", "#E88A00", "#B6601E", "#00BCD4"};
        int indexSexualidade = 0;

        for (Map.Entry<String, Long> entry : sexualidadeMap.entrySet()) {
            quantPorSexualidade.add(DataDashboardResponse.builder()
                    .name(entry.getKey())
                    .value(entry.getValue())
                    .fill(coresSexualidade[indexSexualidade % coresSexualidade.length])
                    .build());
            indexSexualidade++;
        }

        response.add(DashboardResponse.builder()
                .titulo("Distribuição por sexualidade")
                .descricao("Quantidade de colaboradores agrupados por sexualidade")
                .values(quantPorSexualidade)
                .build());

        // PESQUISA ====================================================================================================
        // Quantidade total de pesquisas
        List<DataDashboardResponse> quantPesquisa = new ArrayList<>();
        List<Pesquisa> todasPesquisas = pesquisaRepository.findAll();

        quantPesquisa.add(DataDashboardResponse.builder()
                .name("Total de pesquisas")
                .value((long) todasPesquisas.size())
                .fill("#6366F1")
                .build());

        response.add(DashboardResponse.builder()
                .titulo("Quantidade total de pesquisas")
                .descricao("Número total de pesquisas criadas na plataforma")
                .values(quantPesquisa)
                .build());

        // Quantidade de pesquisas ativas
        List<DataDashboardResponse> quantPesquisaAtivas = new ArrayList<>();
        long pesquisasAtivas = todasPesquisas.stream()
                .filter(p -> p.getAtivo() != null && p.getAtivo())
                .count();

        quantPesquisaAtivas.add(DataDashboardResponse.builder()
                .name("Pesquisas ativas")
                .value(pesquisasAtivas)
                .fill("#10B981")
                .build());

        response.add(DashboardResponse.builder()
                .titulo("Quantidade de pesquisas ativas")
                .descricao("Número de pesquisas que estão atualmente ativas")
                .values(quantPesquisaAtivas)
                .build());

        // Quantidade média de colaboradores por pesquisa
        List<DataDashboardResponse> mediaColabPorPesquisa = new ArrayList<>();
        List<PesquisaColaborador> todasRelacoes = pesquisaColaboradorRepository.findAll();

        if (!todasPesquisas.isEmpty()) {
            Map<Long, Long> colaboradoresPorPesquisa = todasRelacoes.stream()
                    .collect(Collectors.groupingBy(pc -> pc.getPesquisa().getId(), Collectors.counting()));

            double media = colaboradoresPorPesquisa.values().stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);

            mediaColabPorPesquisa.add(DataDashboardResponse.builder()
                    .name("Média de colaboradores")
                    .value((long) Math.round(media))
                    .fill("#F59E0B")
                    .build());
        }

        response.add(DashboardResponse.builder()
                .titulo("Média de colaboradores por pesquisa")
                .descricao("Quantidade média de colaboradores vinculados a cada pesquisa")
                .values(mediaColabPorPesquisa)
                .build());

        // Quantidade de colaboradores que estão em alguma pesquisa
        List<DataDashboardResponse> quantColabEmPesquisa = new ArrayList<>();
        long colaboradoresUnicos = todasRelacoes.stream()
                .map(pc -> pc.getUsuario().getId())
                .distinct()
                .count();

        quantColabEmPesquisa.add(DataDashboardResponse.builder()
                .name("Colaboradores em pesquisas")
                .value(colaboradoresUnicos)
                .fill("#8B5CF6")
                .build());

        response.add(DashboardResponse.builder()
                .titulo("Colaboradores participando de pesquisas")
                .descricao("Quantidade de colaboradores únicos que estão vinculados a pelo menos uma pesquisa")
                .values(quantColabEmPesquisa)
                .build());

        // Quantidade de pesquisas respondidas
        List<DataDashboardResponse> quantPesquisaRespondidas = new ArrayList<>();
        Map<Long, Boolean> pesquisasRespondidas = new HashMap<>();

        for (Pesquisa pesquisa : todasPesquisas) {
            List<PesquisaColaborador> colaboradoresDaPesquisa = pesquisaColaboradorRepository.findByPesquisa(pesquisa);

            if (!colaboradoresDaPesquisa.isEmpty()) {
                boolean todosResponderam = colaboradoresDaPesquisa.stream()
                        .allMatch(PesquisaColaborador::isRespondido);
                pesquisasRespondidas.put(pesquisa.getId(), todosResponderam);
            }
        }

        long totalRespondidas = pesquisasRespondidas.values().stream()
                .filter(respondida -> respondida)
                .count();

        quantPesquisaRespondidas.add(DataDashboardResponse.builder()
                .name("Pesquisas respondidas")
                .value(totalRespondidas)
                .fill("#059669")
                .build());

        response.add(DashboardResponse.builder()
                .titulo("Pesquisas totalmente respondidas")
                .descricao("Quantidade de pesquisas onde todos os colaboradores já responderam")
                .values(quantPesquisaRespondidas)
                .build());

        // Quantidade de pesquisas pendentes
        List<DataDashboardResponse> quantPesquisaPendentes = new ArrayList<>();
        long totalPendentes = todasRelacoes.stream()
                .filter(pc -> !pc.isRespondido())
                .map(pc -> pc.getPesquisa().getId())
                .distinct()
                .count();

        quantPesquisaPendentes.add(DataDashboardResponse.builder()
                .name("Pesquisas pendentes")
                .value(totalPendentes)
                .fill("#EF4444")
                .build());

        response.add(DashboardResponse.builder()
                .titulo("Pesquisas com respostas pendentes")
                .descricao("Quantidade de pesquisas que ainda possuem colaboradores que não responderam")
                .values(quantPesquisaPendentes)
                .build());
        return response;
    }

    public List<DashboardResponse> getDashboardPesquisa(Long pesquisaId) {
        List<DashboardResponse> response = new ArrayList<>();

        Pesquisa pesquisa = pesquisaRepository.getReferenceById(pesquisaId);

        List<Pergunta> perguntas = perguntaRepository.findByPesquisa(pesquisa);

        String[] cores = {"#AC4A00", "#FBBC04", "#E86500", "#E88A00", "#B6601E"};

        for(Pergunta pergunta: perguntas){
            List<Resposta> respostas = respostaRepository.findByPerguntaId(pergunta.getId());

            if(Objects.equals(pergunta.getTipoPergunta().getDescricao(), "descritiva")){
                List<DataDashboardResponse> quantDescritiva = new ArrayList<>();


                for (Resposta resposta: respostas) {
                    quantDescritiva.add(DataDashboardResponse.builder()
                            .name(resposta.getRespostaDescritiva())
                            .build());
                }

                response.add(DashboardResponse.builder()
                        .titulo(pergunta.getPergunta())
                        .descricao(pergunta.getTipoPergunta().getDescricao())
                        .values(quantDescritiva)
                        .build());
            }

            if(Objects.equals(pergunta.getTipoPergunta().getDescricao(), "escala")){
                List<DataDashboardResponse> quantEscala = new ArrayList<>();

                // Agrupar e contar as respostas de escala
                Map<String, Long> escalaMap = respostas.stream()
                        .filter(r -> r.getRespostaEscala() != null)
                        .collect(Collectors.groupingBy(Resposta::getRespostaEscala, Collectors.counting()));

                int indexEscala = 0;

                for (Map.Entry<String, Long> entry : escalaMap.entrySet()) {
                    quantEscala.add(DataDashboardResponse.builder()
                            .name(entry.getKey())
                            .value(entry.getValue())
                            .fill(cores[indexEscala % cores.length])
                            .build());
                    indexEscala++;
                }

                response.add(DashboardResponse.builder()
                        .titulo(pergunta.getPergunta())
                        .descricao(pergunta.getTipoPergunta().getDescricao())
                        .values(quantEscala)
                        .build());
            }

            if(Objects.equals(pergunta.getTipoPergunta().getDescricao(), "opcoes")){
                List<DataDashboardResponse> quantOpcoes = new ArrayList<>();

                Map<String, Long> opcoesMap = new HashMap<>();

                for (Resposta resposta : respostas) {
                    List<String> opcoesSelecionadas = resposta.getRespostaOpcoes();
                    if (opcoesSelecionadas != null) {
                        for (String opcao : opcoesSelecionadas) {
                            opcoesMap.put(opcao, opcoesMap.getOrDefault(opcao, 0L) + 1);
                        }
                    }
                }

                int indexOpcoes = 0;

                for (Map.Entry<String, Long> entry : opcoesMap.entrySet()) {
                    quantOpcoes.add(DataDashboardResponse.builder()
                            .name(entry.getKey())
                            .value(entry.getValue())
                            .fill(cores[indexOpcoes % cores.length])
                            .build());
                    indexOpcoes++;
                }

                response.add(DashboardResponse.builder()
                        .titulo(pergunta.getPergunta())
                        .descricao(pergunta.getTipoPergunta().getDescricao())
                        .values(quantOpcoes)
                        .build());
            }

        }

        return response;
    }
}
