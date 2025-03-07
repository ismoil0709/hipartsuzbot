package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select (count(p) > 0) from Product p where p.category.id = :categoryId")
    boolean existsByCategory(@Param("categoryId") Long categoryId);

}
