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
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToMany
    @ToString.Exclude
    private List<Product> products;
    @NotNull
    @ManyToOne
    private User user;
    @NotNull
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
    @OneToMany
    private List<ProductQuantity> productQuantities;
}
