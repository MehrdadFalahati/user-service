package com.github.mehrdadfalahati.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.io.IOUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConfig {

    private final DataSource dataSource;

    public DbConfig() {
        dataSource = createH2();
        initDDL(dataSource);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    private DataSource createH2() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        return new HikariDataSource(config);
    }

    private void initDDL(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(IOUtils.toString(this.getClass().getResourceAsStream("/script.sql")));
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
