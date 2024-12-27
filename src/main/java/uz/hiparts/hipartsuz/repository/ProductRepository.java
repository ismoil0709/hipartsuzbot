package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
