package uz.hiparts.hipartsuz.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelTransactionDto {
    @JsonProperty(value = "cancel_time")
    private Long cancelTime;

    private Integer state;

    private String transaction;
}
