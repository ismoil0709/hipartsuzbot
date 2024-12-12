package uz.hiparts.hipartsuz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@Builder
@ToString
@SQLRestriction("isActive = true")
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double price;

    private String imgPath;

    private String imgId;

    @ManyToOne
    private Category category;

    private Double discount;

    private Boolean isActive;
}
