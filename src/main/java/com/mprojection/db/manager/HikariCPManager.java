package com.mprojection.db.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.mprojection.db.DatabaseConfig.*;

/**
 * A connection manager, based on HikariCP tool.
 */
@SuppressWarnings("unused")
public final class HikariCPManager implements ConnectionManager {

    //private static final Logger LOGGER = LoggerFactory.getLogger(HikariCPManager.class);
    private static final boolean CACHE_PREPARE_STATEMENTS = true;
    private static final int PREPARE_STATEMENTS_CACHE_SIZE = 250;
    private static final int PREPARE_STATEMENTS_CACHE_SQL_LIMIT = 2048;
    private static final boolean USE_SERVER_PREPARE_STATEMENTS = true;
    private static final Map<String, String> DRIVERS;

    static {
        DRIVERS = new HashMap<>();
        DRIVERS.put("mysql", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        DRIVERS.put("postgres", "org.postgresql.ds.PGSimpleDataSource");
    }

    private HikariDataSource dataSource;

    /**
     * Instantiates a new manager.
     */
    public HikariCPManager() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(DRIVERS.get(getRdbms()));
        config.setInitializationFailFast(true);
        config.addDataSourceProperty("serverName", getServer());
        config.addDataSourceProperty("port", getPort());
        config.addDataSourceProperty("databaseName", getDatabase());
        config.addDataSourceProperty("user", getUser());
        config.addDataSourceProperty("password", getPassword());
        config.addDataSourceProperty("cachePrepStmts", CACHE_PREPARE_STATEMENTS);
        config.addDataSourceProperty("prepStmtCacheSize", PREPARE_STATEMENTS_CACHE_SIZE);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", PREPARE_STATEMENTS_CACHE_SQL_LIMIT);
        config.addDataSourceProperty("useServerPrepStmts", USE_SERVER_PREPARE_STATEMENTS);
        config.setTransactionIsolation("TRANSACTION_" + getTransactionIsolation());
        dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            // LOGGER.error("Connection wasn't set", e);
            throw new IllegalStateException("Connection wasn't set", e);
        }
    }

    @Override
    public void shutdown() {
        dataSource.close();
    }

}
