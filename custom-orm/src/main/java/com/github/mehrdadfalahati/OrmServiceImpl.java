package com.github.mehrdadfalahati;

import com.github.mehrdadfalahati.entity.OrmMetaDataService;
import com.github.mehrdadfalahati.entity.OrmMetaDataServiceImpl;
import com.github.mehrdadfalahati.entity.meta.BeanFieldInfo;
import com.github.mehrdadfalahati.entity.meta.TableMetaInfo;
import com.github.mehrdadfalahati.exception.OrmServiceException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class OrmServiceImpl implements OrmService {
    private DataSource dataSource;
    private OrmMetaDataService metaDataService = new OrmMetaDataServiceImpl(this);

    public OrmServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> T findById(Class<T> objectClass, Object key) {
        var tableInfo = metaDataService.getTableMetaInfo(objectClass);
        String sql = " SELECT * FROM " + tableInfo.getTableName() + " WHERE " + tableInfo.getIdRow().getFirst() + " = ?;";

        List<T> searchResult = findAll(objectClass, sql, Collections.singletonList(key));
        if (searchResult.isEmpty()) {
            throw new OrmServiceException("Entity not found");
        }
        if (searchResult.size() > 2) {
            throw new OrmServiceException("Bad id");
        }
        return searchResult.get(0);
    }

    @Override
    public <T> List<T> findAll(Class<T> objectClass) {
        var tableInfo = metaDataService.getTableMetaInfo(objectClass);
        String sql = " SELECT * FROM " + tableInfo.getTableName() + ";";
        return findAll(objectClass, sql, Collections.emptyList());
    }

    private <T> List<T> findAll(Class<T> objectClass, String sql, List<Object> params) {
        List<T> result = new ArrayList<>();
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            metaDataService.fillPreparedStatement(preparedStatement, params);
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(metaDataService.resultSetToObject(resultSet, objectClass));
                }
            }
            log.info("exec sql {}", sql);
        } catch (SQLException e) {
            throw new OrmServiceException(e);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(T object) {
        var tableInfo = metaDataService.getTableMetaInfo(object.getClass());

        List<Object> values = new ArrayList<>();
        String sql = createSqlQuery(object, tableInfo, values);

        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            metaDataService.fillPreparedStatement(preparedStatement, values);
            preparedStatement.executeUpdate();
            log.info("exec sql {}", sql);

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return findById((Class<? extends T>) object.getClass(), generatedKeys.getObject(1));
                } else {
                    throw new OrmServiceException("Nothing to create");
                }
            }
        } catch (SQLException e) {
            throw new OrmServiceException(e);
        }
    }

    private <T> String createSqlQuery(T object, TableMetaInfo tableInfo, List<Object> values) {
        var sqlRows = new StringBuilder();

        for (Map.Entry<String, BeanFieldInfo> entry : tableInfo.getBaseRows().entrySet()) {
            sqlRows.append(entry.getKey()).append(", ");
            values.add(metaDataService.getFieldValue(entry.getValue(), object));
        }

        var sqlQuestionValues = new StringBuilder();
        sqlQuestionValues.append("?, ".repeat(Math.max(0, tableInfo.getBaseRows().size() - 1)));
        sqlQuestionValues.append("?");

        String sql;
        if (sqlRows.length() > 3) {
            sql = "INSERT INTO " + tableInfo.getTableName() + " (" + sqlRows.substring(0, sqlRows.length() - 2) + ") " +
                    " VALUES (" + sqlQuestionValues + " );";
        } else {
            throw new OrmServiceException("Could not create sql INSERT query");
        }
        return sql;
    }

    @Override
    public <T> boolean update(T object) {
        var tableInfo = metaDataService.getTableMetaInfo(object.getClass());

        var set = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (Map.Entry<String, BeanFieldInfo> entry : tableInfo.getBaseRows().entrySet()) {
            set.append(entry.getKey()).append(" = ?, ");
            values.add(metaDataService.getFieldValue(entry.getValue(), object));
        }

        String sql;
        if (tableInfo.getIdRow() != null && set.length() > 3) {
            sql = "UPDATE " + tableInfo.getTableName() +
                    " SET " + set.substring(0, set.length() - 2) +
                    " WHERE " + tableInfo.getIdRow().getFirst() + " = ?;";
            values.add(metaDataService.getFieldValue(tableInfo.getIdRow().getSecond(), object));

            try (var connection = dataSource.getConnection();
                 var preparedStatement = connection.prepareStatement(sql)) {
                metaDataService.fillPreparedStatement(preparedStatement, values);
                if (preparedStatement.executeUpdate() != 0) {
                    log.info("exec sql {}", sql);
                    return true;
                }
            } catch (SQLException e) {
                throw new OrmServiceException(e);
            }
        }
        return false;

    }

    @Override
    public <T> boolean delete(T object) {

        var tableInfo = metaDataService.getTableMetaInfo(object.getClass());

        String sql = "DELETE FROM " + tableInfo.getTableName() +
                " WHERE " + tableInfo.getIdRow().getFirst() + " = ? ;";

        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            if (tableInfo.getIdRow() != null) {
                preparedStatement.setObject(1, tableInfo.getIdRow().getSecond().getGetter().invoke(object));
                if (preparedStatement.executeUpdate() != 0) {
                    log.info("exec sql {}", sql);
                    return true;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | SQLException e) {
            throw new OrmServiceException(e);
        }

        return false;
    }

    @Override
    public <T> boolean deleteAll(T object) {
        var tableInfo = metaDataService.getTableMetaInfo(object.getClass());

        String sql = "DELETE FROM " + tableInfo.getTableName() + " ;";

        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            if (preparedStatement.executeUpdate() != 0) {
                log.info("exec sql {}", sql);
                return true;
            }
        } catch (SQLException e) {
            throw new OrmServiceException(e);
        }

        return false;
    }
}
