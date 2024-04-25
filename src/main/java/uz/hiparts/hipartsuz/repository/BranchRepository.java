package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.Branch;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
}
