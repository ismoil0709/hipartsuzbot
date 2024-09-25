package uz.hiparts.hipartsuz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CompleteResponseDto {
    @JsonProperty("click_trans_id")
    private Long clickTransId;
    @JsonProperty("merchant_trans_id")
    private String merchantTransId;
    @JsonProperty("merchant_confirm_id")
    private Integer merchantConfirmId;
    private Integer error;
    @JsonProperty("error_note")
    private String errorNote;
}
