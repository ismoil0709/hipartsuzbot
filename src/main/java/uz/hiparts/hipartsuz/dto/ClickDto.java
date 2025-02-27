package uz.hiparts.hipartsuz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClickDto {

    @JsonProperty(value = "click_trans_id")
    private Long clickTransId;

    @JsonProperty(value = "service_id")
    private Long serviceId;

    @JsonProperty(value = "click_paydoc_id")
    private Long clickPaydocId;

    @JsonProperty(value = "merchant_trans_id")
    private String merchantTransId;

    @JsonProperty(value = "merchant_prepare_id")
    private Long merchantPrepareId;

    @JsonProperty(value = "merchant_confirm_id")
    private Long merchantConfirmId;

    @JsonProperty(value = "amount")
    private float amount;

    @JsonProperty(value = "action")
    private Long action;

    @JsonProperty(value = "error")
    private Long error;

    @JsonProperty(value = "error_note")
    private String errorNote;

    @JsonProperty(value = "sign_time")
    private String signTime;

    @JsonProperty(value = "sign_string")
    private String signString;

    public ClickDto(Map<String, Object> request) {
        this.clickTransId = request.containsKey("click_trans_id") ? Long.valueOf(request.get("click_trans_id").toString()) : null;
        this.serviceId = request.containsKey("service_id") ? Long.valueOf(request.get("service_id").toString()) : null;
        this.clickPaydocId = request.containsKey("click_paydoc_id") ? Long.valueOf(request.get("click_paydoc_id").toString()) : null;
        this.merchantTransId = request.containsKey("merchant_trans_id") ? request.get("merchant_trans_id").toString() : null;
        this.merchantPrepareId = request.containsKey("merchant_prepare_id") ? Long.valueOf(request.get("merchant_prepare_id").toString()) : null;
        this.merchantConfirmId = request.containsKey("merchant_confirm_id") ? Long.valueOf(request.get("merchant_confirm_id").toString()) : null;
        this.amount = request.containsKey("amount") ? Float.parseFloat(request.get("amount").toString()) : 0.0f;
        this.action = request.containsKey("action") ? Long.valueOf(request.get("action").toString()) : null;
        this.error = request.containsKey("error") ? Long.valueOf(request.get("error").toString()) : null;
        this.errorNote = request.containsKey("error_note") ? request.get("error_note").toString() : null;
        this.signTime = request.containsKey("sign_time") ? request.get("sign_time").toString() : null;
        this.signString = request.containsKey("sign_string") ? request.get("sign_string").toString() : null;
    }
}
