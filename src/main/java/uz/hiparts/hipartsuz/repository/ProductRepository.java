package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.model.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNameAndActive(String name, boolean isActive);
    Optional<Product> findByIdAndActive(Long id, boolean isActive);
    Optional<Product> findByCategoryAndActive(Category category, boolean isActive);
}
