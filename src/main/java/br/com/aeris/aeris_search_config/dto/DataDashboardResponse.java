package br.com.aeris.aeris_search_config.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataDashboardResponse {
    private String name;
    private Long value;
    private String fill;
}
