package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.ClickPayment;

@Repository
public interface ClickPaymentRepository extends JpaRepository<ClickPayment, Long> {
}
