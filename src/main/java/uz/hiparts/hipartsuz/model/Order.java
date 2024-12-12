package uz.hiparts.hipartsuz.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.hiparts.hipartsuz.model.enums.OrderType;
import uz.hiparts.hipartsuz.model.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    private OrderType orderType;

    private String branch;

    private String address;

    private Double lat;

    private Double lon;

    @NotNull
    private Double totalPrice;

    @NotNull
    private LocalDateTime time;

    @NotNull
    private PaymentType paymentType;

    private String phoneNumber;

    private String comment;

    private boolean active;

    private String invoiceId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductQuantity> productQuantities = new ArrayList<>();

    private boolean isPaid;

    private boolean isCancelled;

}
