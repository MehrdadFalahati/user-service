package com.github.mehrdadfalahati.domain.repository;

import com.github.mehrdadfalahati.OrmService;
import com.github.mehrdadfalahati.OrmServiceImpl;
import com.github.mehrdadfalahati.domain.entity.Branch;

import javax.sql.DataSource;
import java.util.List;

public class BranchRepository {

    private OrmService ormService;

    public BranchRepository(DataSource dataSource) {
        this.ormService = new OrmServiceImpl(dataSource);
    }

    public Branch create(Branch branch) {
        return ormService.create(branch);
    }

    public boolean update(Branch branch) {
        return ormService.update(branch);
    }

    public Branch findById(Long id) {
        return ormService.findById(Branch.class, id);
    }

    public boolean deleteById(Long id) {
        Branch branch = findById(id);
        return ormService.delete(branch);
    }

    public List<Branch> findAll() {
        return ormService.findAll(Branch.class);
    }

    public boolean deleteAll() {
        return ormService.deleteAll(new Branch());
    }
}
