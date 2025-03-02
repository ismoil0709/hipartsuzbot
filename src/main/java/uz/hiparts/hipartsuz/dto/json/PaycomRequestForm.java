package uz.hiparts.hipartsuz.dto.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaycomRequestForm {

    private Long id;

    private String method;

    private Params params;
}
