package uz.eskiz.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendSmsResponseDTO {
    private String id;
    private String message;
    private String status;
}
