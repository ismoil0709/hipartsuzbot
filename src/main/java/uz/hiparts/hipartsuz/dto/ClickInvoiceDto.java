package uz.hiparts.hipartsuz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ClickInvoiceDto {
    @JsonProperty("error_code")
    private Integer errorCode;
    @JsonProperty("error_note")
    private String errorNote;
    @JsonProperty("invoice_id")
    private Long invoiceId;
}
