package uz.hiparts.hipartsuz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClickDto {

    @JsonProperty(value = "click_trans_id")
    private Integer clickTransId;

    @JsonProperty(value = "service_id")
    private Integer serviceId;

    @JsonProperty(value = "click_paydoc_id")
    private Integer clickPaydocId;

    @JsonProperty(value = "merchant_trans_id")
    private String merchantTransId;

    @JsonProperty(value = "merchant_prepare_id")
    private Long merchantPrepareId;

    @JsonProperty(value = "merchant_confirm_id")
    private Long merchantConfirmId;

    @JsonProperty(value = "amount")
    private float amount;

    @JsonProperty(value = "action")
    private Integer action;

    @JsonProperty(value = "error")
    private Integer error;

    @JsonProperty(value = "error_note")
    private String errorNote;

    @JsonProperty(value = "sign_time")
    private String signTime;

    @JsonProperty(value = "sign_string")
    private String signString;
}
