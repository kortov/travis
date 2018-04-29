package juja.sqlcmd;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcDatabaseManagerTest extends AbstractDatabaseManagerTest {
    private static final String ADMIN_DB_NAME = "postgres";
    private static final String DB_ADMIN_LOGIN = "postgres";
    private static final String DB_ADMIN_PASSWORD = "postgres";
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/";
    private static Connection connection;

    @BeforeClass
    public static void setTestingEnvironment() throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL + ADMIN_DB_NAME, DB_ADMIN_LOGIN, DB_ADMIN_PASSWORD);
        executeSqlQuery("DROP DATABASE IF EXISTS " + TEST_DB_NAME);
        executeSqlQuery("CREATE DATABASE " + TEST_DB_NAME + " OWNER =" + DB_USER_LOGIN);
        connection.close();
        connection = DriverManager.getConnection(JDBC_URL + TEST_DB_NAME, DB_ADMIN_LOGIN, DB_ADMIN_PASSWORD);
        recreateDbSchema();
        connection.close();
        connection = DriverManager.getConnection(JDBC_URL + TEST_DB_NAME, DB_USER_LOGIN, DB_USER_PASSWORD);
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    private static void executeSqlQuery(String sqlQuery) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlQuery);
        }
    }

    private static void recreateDbSchema() throws SQLException {
        executeSqlQuery("DROP SCHEMA IF EXISTS public CASCADE");
        executeSqlQuery("CREATE SCHEMA public AUTHORIZATION " + DB_USER_LOGIN);
    }

    @Before
    public void setUp() throws SQLException {
        recreateDbSchema();
        super.init(new JdbcDatabaseManager());
        databaseManager.connect(TEST_DB_NAME, DB_USER_LOGIN, DB_USER_PASSWORD);
    }

    @After
    public void closeResources() throws SQLException {
        databaseManager.close();
    }

    void createTestTableWithIdAndName(String tableName) throws SQLException {
        executeSqlQuery("CREATE TABLE " + tableName +
                "(id INTEGER, name VARCHAR(128))");
    }
}