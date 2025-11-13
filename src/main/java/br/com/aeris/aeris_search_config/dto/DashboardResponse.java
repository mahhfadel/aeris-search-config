package br.com.aeris.aeris_search_config.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private String titulo;
    private String descricao;
    private List<DataDashboardResponse> values;
}
