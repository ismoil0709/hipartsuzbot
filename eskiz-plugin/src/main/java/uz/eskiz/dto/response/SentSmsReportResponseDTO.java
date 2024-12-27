package uz.eskiz.dto.response;

import lombok.Data;

@Data
public class SentSmsReportResponseDTO {
    private String month;
    private String status;
    private Integer packets;
    private Integer sent_packets;
}
