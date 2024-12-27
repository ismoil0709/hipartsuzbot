package uz.eskiz.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class EskizResponseDTO {
    private Boolean success;
    private List<TemplateResponseDTO> result;
}
