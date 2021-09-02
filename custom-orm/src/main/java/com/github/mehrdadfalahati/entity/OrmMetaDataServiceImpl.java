package com.github.mehrdadfalahati.entity;

import com.github.mehrdadfalahati.OrmService;
import com.github.mehrdadfalahati.annotations.Column;
import com.github.mehrdadfalahati.annotations.Table;
import com.github.mehrdadfalahati.entity.meta.BeanFieldInfo;
import com.github.mehrdadfalahati.entity.meta.Pair;
import com.github.mehrdadfalahati.entity.meta.TableMetaInfo;
import com.github.mehrdadfalahati.exception.OrmMetaDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class OrmMetaDataServiceImpl implements OrmMetaDataService {
    private final OrmService ormService;
    private Map<Class, TableMetaInfo> tableMetaInfoCache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T initProxyObject(Class<T> objectClass) {
        var enhancer = new Enhancer();
        enhancer.setSuperclass(objectClass);
        enhancer.setCallback((MethodInterceptor) (obj, method, args1, proxy) -> proxy.invokeSuper(obj, args1));
        log.info("init proxy object {}", objectClass.getSimpleName());
        return (T) enhancer.create();

    }

    private List<BeanFieldInfo> getFieldsInfo(Class<?> objectClass) {
        try {
            return createBeanFieldInfos(objectClass);
        } catch (NoSuchFieldException | IntrospectionException e) {
            throw new OrmMetaDataException(e);
        }
    }

    private List<BeanFieldInfo> createBeanFieldInfos(Class<?> objectClass) throws IntrospectionException, NoSuchFieldException {
        List<BeanFieldInfo> beanFieldInfoList = new ArrayList<>();
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(objectClass).getPropertyDescriptors()) {
            if (hasReadAndWriteMethod(propertyDescriptor)) {
                var field = objectClass.getDeclaredField(propertyDescriptor.getName());
                beanFieldInfoList.add(
                        new BeanFieldInfo(
                                field,
                                propertyDescriptor.getReadMethod(),
                                propertyDescriptor.getWriteMethod())
                );
            }
        }
        return beanFieldInfoList;
    }


    private boolean hasReadAndWriteMethod(PropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null;
    }

    @Override
    public TableMetaInfo getTableMetaInfo(Class<?> objectClass) {
        if (Enhancer.isEnhanced(objectClass)) {
            objectClass = objectClass.getSuperclass();
        }
        if (tableMetaInfoCache.containsKey(objectClass)) {
            return tableMetaInfoCache.get(objectClass);
        }

        if (objectClass.getAnnotation(Table.class) == null) {
            throw new OrmMetaDataException("Table annotation not found");
        }

        var tableMetaInfo = new TableMetaInfo();
        tableMetaInfo.setTableName(objectClass.getAnnotation(Table.class).name());
        tableMetaInfo.setBaseRows(new HashMap<>());

        List<BeanFieldInfo> beanFieldInfoList = getFieldsInfo(objectClass);

        for (BeanFieldInfo beanFieldInfo : beanFieldInfoList) {
            var column = beanFieldInfo.getField().getAnnotation(Column.class);
            if (column != null) {
                if (!column.primaryKey()) {
                    tableMetaInfo.getBaseRows().put(column.name(), beanFieldInfo);
                } else {
                    tableMetaInfo.setIdRow(Pair.of(column.name(), beanFieldInfo));
                }
            }
        }

        if (!beanFieldInfoList.isEmpty()) {
            tableMetaInfoCache.put(objectClass, tableMetaInfo);
            return tableMetaInfo;
        } else {
            throw new OrmMetaDataException("not mapped fields found");
        }
    }

    private List<String> getColumnsList(ResultSet rs) {
        List<String> columnsList = new ArrayList<>();
        try {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columns = rsMetaData.getColumnCount();
            for (var x = 1; x <= columns; x++) {
                columnsList.add(rsMetaData.getColumnName(x).toLowerCase());
            }
        } catch (SQLException e) {
            throw new OrmMetaDataException(e);
        }
        return columnsList;
    }


    @Override
    public <T> T fillResultSetToObject(ResultSet resultSet, T object) {
        Class<?> objectClass = Enhancer.isEnhanced(object.getClass())
                ? object.getClass().getSuperclass()
                : object.getClass();

        List<String> columnsList = getColumnsList(resultSet);
        var tableMetaInfo = getTableMetaInfo(objectClass);
        try {
            Pair<String, BeanFieldInfo> idRow = tableMetaInfo.getIdRow();
            if (columnsList.contains(idRow.getFirst().toLowerCase())) {
                idRow.getSecond().getSetter().invoke(object, resultSet.getObject(idRow.getFirst().toLowerCase()));
            } else if (columnsList.size() == 1 && columnsList.contains("scope_identity()")) {
                idRow.getSecond().getSetter().invoke(object, resultSet.getObject("scope_identity()"));
                return object;
            }
            for (String baseRow : tableMetaInfo.getBaseRows().keySet()) {
                var beanFieldInfo = tableMetaInfo.getBaseRows().get(baseRow);
                if (columnsList.contains(baseRow.toLowerCase())) {
                    beanFieldInfo.getSetter().invoke(object, resultSet.getObject(baseRow.toLowerCase()));
                }
            }

        } catch (IllegalAccessException | InvocationTargetException | SQLException e) {
            throw new OrmMetaDataException(e);
        }

        return object;
    }

    @Override
    public <T> T resultSetToObject(ResultSet resultSet, Class<T> objectClass) {
        var object = initProxyObject(objectClass);
        return fillResultSetToObject(resultSet, object);
    }

    @Override
    public void fillPreparedStatement(PreparedStatement preparedStatement, List<Object> values) throws SQLException {
        for (var i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            int parameterIndex = i + 1;
            if (value instanceof Date) {
                preparedStatement.setDate(parameterIndex, new java.sql.Date((((Date) value).getTime())));
            } else {
                preparedStatement.setObject(parameterIndex, value);
            }
        }
    }

    @Override
    public Object getFieldValue(BeanFieldInfo beanFieldInfo, Object object) {
        try {
            return beanFieldInfo.getGetter().invoke(object);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new OrmMetaDataException();
        }
    }
}
