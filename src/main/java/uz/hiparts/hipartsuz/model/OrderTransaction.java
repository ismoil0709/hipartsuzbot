package uz.hiparts.hipartsuz.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OrderTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    private Timestamp transactionCreationTime;

    private Timestamp performTime;

    private Timestamp cancelTime;

    private Integer reason;

    private Integer state;

    @JoinColumn(insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    public OrderTransaction(String transactionId, Timestamp transactionCreationTime, Integer state, Long orderId) {
        this.transactionId = transactionId;
        this.transactionCreationTime = transactionCreationTime;
        this.state = state;
        this.orderId = orderId;
    }
}
