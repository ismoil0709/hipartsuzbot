package uz.eskiz.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TemplateResponseDTO {
    private int id;
    private String template;

    @JsonProperty("original_text")
    private String originalText;

    private String status;

}
