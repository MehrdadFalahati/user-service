package com.github.mehrdadfalahati.domain.repository;

import com.github.mehrdadfalahati.OrmService;
import com.github.mehrdadfalahati.OrmServiceImpl;
import com.github.mehrdadfalahati.domain.entity.User;

import javax.sql.DataSource;
import java.util.List;

public class UserRepository {
    private OrmService ormService;

    public UserRepository(DataSource dataSource) {
        this.ormService = new OrmServiceImpl(dataSource);
    }

    public User create(User user) {
        return ormService.create(user);
    }

    public boolean update(User user) {
        return ormService.update(user);
    }

    public User findById(Long id) {
        return ormService.findById(User.class, id);
    }

    public boolean deleteById(Long id) {
        User user = findById(id);
        return ormService.delete(user);
    }

    public List<User> findAll() {
        return ormService.findAll(User.class);
    }

    public boolean deleteAll() {
        return ormService.deleteAll(new User());
    }
}
