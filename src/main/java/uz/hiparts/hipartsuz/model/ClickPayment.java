package uz.hiparts.hipartsuz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "click_payments")
public class ClickPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer clickTransId;

    private Integer serviceId;

    //Номер платежа в системе CLICK. Отображается в СМС у клиента при оплате.
    private Integer clickPaydocId;

    private String merchantTransId;

    private float amount;

    private Integer error;

    private String errorNote;

    private boolean cancelled;
}
