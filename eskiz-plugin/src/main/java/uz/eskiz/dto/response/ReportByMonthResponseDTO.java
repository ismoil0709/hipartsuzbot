package uz.eskiz.dto.response;

import lombok.Data;

@Data
public class ReportByMonthResponseDTO {
    private Integer year;
    private Integer month;
    private Integer ad_parts;
    private Integer ad_spent;
    private Integer parts;
    private Integer spent;
    private Integer total_parts;
    private Integer total_spent;
}
