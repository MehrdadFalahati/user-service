package com.github.mehrdadfalahati;

import com.github.mehrdadfalahati.domain.entity.Branch;
import com.github.mehrdadfalahati.domain.entity.User;
import com.github.mehrdadfalahati.domain.repository.BranchRepository;
import com.github.mehrdadfalahati.domain.repository.UserRepository;

public class Bootstrap {
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    public Bootstrap(UserRepository userRepository, BranchRepository branchRepository) {
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
    }

    public void createUsers() {
        for (int i = 0; i < 5; i++) {
            var user = User.builder()
                    .username("m.falahati" + i)
                    .firstName("Mehrdad" + i)
                    .lastName("Falahati" + i)
                    .build();
            userRepository.create(user);
        }
    }

    public void createBranches() {
        for (int i = 0; i < 5; i++) {
            var branch = Branch.builder()
                    .address("Tehran" + i)
                    .status(true)
                    .branchName("branch" + i)
                    .build();
            branchRepository.create(branch);
        }
    }
}
