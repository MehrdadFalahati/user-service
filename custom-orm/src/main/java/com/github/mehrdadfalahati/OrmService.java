package com.github.mehrdadfalahati;

import java.util.List;

public interface OrmService {
    <T> T findById(Class<T> objectClass, Object key);

    <T> List<T> findAll(Class<T> objectClass);

    <T> T create(T object);

    <T> boolean update(T object);

    <T> boolean delete(T object);

    <T> boolean deleteAll(T object);
}
