package uz.hiparts.hipartsuz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PrepareRequestDto {
    @JsonProperty("click_trans_id")
    private Long clickTransId;
    @JsonProperty("service_id")
    private Integer serviceId;
    @JsonProperty("click_paydoc_id")
    private Long clickPaydocId;
    @JsonProperty("merchant_trans_id")
    private String merchantTransId;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("action")
    private Integer action;
    @JsonProperty("error")
    private Integer error;
    @JsonProperty("error_note")
    private String errorNote;
    @JsonProperty("sign_time")
    private String signTime;
    @JsonProperty("sign_string")
    private String signString;
}
