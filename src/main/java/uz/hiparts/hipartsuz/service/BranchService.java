package uz.hiparts.hipartsuz.service;

import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.model.Branch;

import java.util.List;

@Service
public interface BranchService {
    Branch create(Branch branch);
    Branch getById(Long id);
    List<Branch> getAll();
    void deleteById(Long id);
}
