package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.model.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.name = ?1 AND p.isActive = ?2")
    Optional<Product> findByNameAndActive(String name, boolean active);
    @Query("SELECT p FROM Product p WHERE p.id = ?1 AND p.isActive = ?2")
    Optional<Product> findByIdAndActive(Long id, boolean active);
    @Query("SELECT p FROM Product p WHERE p.category = ?1 AND p.isActive = ?2")
    Optional<Product> findByCategoryAndActive(Category category, boolean active);
}
