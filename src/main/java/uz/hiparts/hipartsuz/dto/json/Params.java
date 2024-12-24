package uz.hiparts.hipartsuz.dto.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Params {

    private String id;

    private Account account;

    private Double amount;

    private Long time;

    private Integer reason;

    private Long from;

    private Long to;
}
