package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.exception.NotFoundException;
import uz.hiparts.hipartsuz.model.Branch;
import uz.hiparts.hipartsuz.repository.BranchRepository;
import uz.hiparts.hipartsuz.service.BranchService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public Branch create(Branch branch) {
        return branchRepository.save(Branch.builder()
                .name(branch.getName())
                .lon(branch.getLon())
                .lat(branch.getLat())
                .address(branch.getAddress())
                .build());
    }

    @Override
    public void delete(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Branch"));
        branchRepository.deleteById(id);
    }

    @Override
    public Branch getById(Long id) {
        return branchRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Branch not found")
        );
    }

    @Override
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }

}
