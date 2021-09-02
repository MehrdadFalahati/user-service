package com.github.mehrdadfalahati;

import com.github.mehrdadfalahati.config.DbConfig;
import com.github.mehrdadfalahati.domain.repository.BranchRepository;
import com.github.mehrdadfalahati.domain.repository.UserRepository;


public class Application {

    public static void main(String[] args) {
        var config = new DbConfig();
        var userRepository = new UserRepository(config.getDataSource());
        var branchRepository = new BranchRepository(config.getDataSource());
        var bootstrap = new Bootstrap(userRepository, branchRepository);
        bootstrap.createUsers();
        bootstrap.createBranches();

        var users = userRepository.findAll();
        users.forEach(System.out::println);

        var branches = branchRepository.findAll();
        branches.forEach(System.out::println);

        var oneUser = userRepository.findById(1L);
        System.out.println(oneUser);

        userRepository.deleteById(1L);

        var newUsers = userRepository.findAll();
        System.out.println(newUsers.size() == 4);
    }
}
