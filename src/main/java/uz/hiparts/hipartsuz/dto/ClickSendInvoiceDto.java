package uz.hiparts.hipartsuz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ClickSendInvoiceDto {
    @JsonProperty("service_id")
    private Integer serviceId;
    private Float amount;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("merchant_trans_id")
    private String merchantTransId;
}
