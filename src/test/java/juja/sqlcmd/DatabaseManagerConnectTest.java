package juja.sqlcmd;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DatabaseManagerConnectTest {
    private static final String DB_NAME = "sqlcmd";
    private static final String TEST_DB_NAME = "sqlcmd_test";
    private static final String DB_ADMIN_LOGIN = "postgres";
    private static final String DB_ADMIN_PASSWORD = "postgres";
    private static final String DB_USER_LOGIN = "sqlcmd";
    private static final String DB_USER_PASSWORD = "sqlcmd";
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/";
    private static Connection connection;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private PrintStream originalErr;
    private DatabaseManager databaseManager;

    @BeforeClass
    public static void setTestingEnvironment() throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL + DB_NAME, DB_ADMIN_LOGIN, DB_ADMIN_PASSWORD);
        executeSqlQuery("DROP DATABASE IF EXISTS " + TEST_DB_NAME);
        executeSqlQuery("CREATE DATABASE " + TEST_DB_NAME + " OWNER =" + DB_USER_LOGIN);
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

    @Before
    public void setUpStreamsAndDbManager() {
        databaseManager = new DatabaseManager();
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() throws SQLException {
        databaseManager.close();
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void connectWithAllValidParameters() {
        assertTrue(databaseManager.connect(TEST_DB_NAME, DB_USER_LOGIN, DB_USER_PASSWORD));
    }

    @Test
    public void connectWithInvalidPassword() {
        assertFalse(databaseManager.connect(TEST_DB_NAME, DB_USER_LOGIN, "wrongPass"));
    }

    @Test
    public void connectWithInvalidUserName() {
        assertFalse(databaseManager.connect(TEST_DB_NAME, "noSqlcmd", DB_USER_PASSWORD));
    }

    @Test
    public void connectWithInvalidDbName() {
        assertFalse(databaseManager.connect("doesNotExist", DB_USER_LOGIN, DB_USER_PASSWORD));
    }

    @Test
    public void initDatabaseManagerWithInvalidJdbcDriver() {
        databaseManager = new DatabaseManager("noDriver", "jdbc:postgresql://localhost:5432/");
        assertFalse(databaseManager.connect(TEST_DB_NAME, DB_USER_LOGIN, DB_USER_PASSWORD));
    }

    @Test
    public void initDatabaseManagerWithInvalidJdbcUrl() {
        databaseManager = new DatabaseManager("org.postgresql.Driver", "noJdbcUrl");
        assertFalse(databaseManager.connect(TEST_DB_NAME, DB_USER_LOGIN, DB_USER_PASSWORD));
    }
}
