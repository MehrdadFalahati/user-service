package com.github.mehrdadfalahati;

import com.github.mehrdadfalahati.config.DbConfig;
import com.github.mehrdadfalahati.domain.entity.Branch;
import com.github.mehrdadfalahati.domain.entity.User;
import com.github.mehrdadfalahati.domain.repository.BranchRepository;
import com.github.mehrdadfalahati.domain.repository.UserRepository;
import com.github.mehrdadfalahati.exception.OrmServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {
    DbConfig config = new DbConfig();
    UserRepository userRepository = new UserRepository(config.getDataSource());
    BranchRepository branchRepository = new BranchRepository(config.getDataSource());

    @AfterEach
    void removedData() {
        userRepository.deleteAll();
        branchRepository.deleteAll();
    }

    @Test
    void test_create_users() {

        for (int i = 0; i < 5; i++) {
            var user = User.builder()
                    .username("m.falahati" + i)
                    .firstName("Mehrdad" + i)
                    .lastName("Falahati" + i)
                    .build();
            userRepository.create(user);
        }

        var users = userRepository.findAll();
        assertEquals(5, users.size());
    }

    @Test
    void test_update_user() {
        var user = User.builder()
                .username("m.falahati")
                .firstName("Mehrdad")
                .lastName("Falahati")
                .build();
        var result = userRepository.create(user);

        result.setFirstName("Hadi");
        result.setUsername("h.falahati");
        userRepository.update(result);

        var newUser = userRepository.findById(result.getId());
        assertEquals("Hadi", newUser.getFirstName());
        assertEquals("h.falahati", newUser.getUsername());
    }

    @Test
    void test_find_one_user() {
        var user = User.builder()
                .username("m.falahati")
                .firstName("Mehrdad")
                .lastName("Falahati")
                .build();
        var result = userRepository.create(user);

        var newUser = userRepository.findById(result.getId());
        assertEquals(user.getFirstName(), newUser.getFirstName());
        assertEquals(user.getUsername(), newUser.getUsername());
    }

    @Test
    void test_delete_user() {
        var user = User.builder()
                .username("m.falahati")
                .firstName("Mehrdad")
                .lastName("Falahati")
                .build();
        var result = userRepository.create(user);

        assertTrue(userRepository.deleteById(result.getId()));

        assertThrows(OrmServiceException.class, () -> userRepository.findById(result.getId()));
    }

    @Test
    void test_create_Branches() {

        for (int i = 0; i < 5; i++) {
            var branch = Branch.builder()
                    .address("Tehran" + i)
                    .status(true)
                    .branchName("branch" + i)
                    .build();
            branchRepository.create(branch);
        }

        var branches = branchRepository.findAll();
        assertEquals(5, branches.size());
    }

    @Test
    void test_update_Branch() {

        var branch = Branch.builder()
                .address("Tehran")
                .status(true)
                .branchName("branch")
                .build();
        var result = branchRepository.create(branch);

        result.setAddress("Qom");
        result.setStatus(false);
        branchRepository.update(result);

        var newBranch = branchRepository.findById(result.getId());
        assertEquals("Qom", newBranch.getAddress());
        assertFalse(newBranch.getStatus());
    }

    @Test
    void test_delete_Branch() {

        var branch = Branch.builder()
                .address("Tehran")
                .status(true)
                .branchName("branch")
                .build();
        var result = branchRepository.create(branch);

        assertTrue(branchRepository.deleteById(result.getId()));

        assertThrows(OrmServiceException.class, () -> branchRepository.findById(result.getId()));
    }
}