package juja.sqlcmd;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

public class DatabaseManagerTest {
    private static final String DB_NAME = "sqlcmd";
    private static final String TEST_DB_NAME = "sqlcmd_test";
    private static final String DB_ADMIN_LOGIN = "postgres";
    private static final String DB_ADMIN_PASSWORD = "postgres";
    private static final String DB_USER_LOGIN = "sqlcmd";
    private static final String DB_USER_PASSWORD = "sqlcmd";
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/";
    private static Connection connection;

    private DatabaseManager databaseManager;

    @BeforeClass
    public static void setTestingEnvironment() throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL + DB_NAME, DB_ADMIN_LOGIN, DB_ADMIN_PASSWORD);
        executeSqlQuery("DROP DATABASE IF EXISTS " + TEST_DB_NAME);
        executeSqlQuery("CREATE DATABASE " + TEST_DB_NAME + " OWNER =" + DB_USER_LOGIN);
        connection.close();
        connection = DriverManager.getConnection(JDBC_URL + TEST_DB_NAME, DB_ADMIN_LOGIN, DB_ADMIN_PASSWORD);
        executeSqlQuery("ALTER SCHEMA public OWNER TO " + DB_USER_LOGIN);
        connection.close();
        connection = DriverManager.getConnection(JDBC_URL + TEST_DB_NAME, DB_USER_LOGIN, DB_USER_PASSWORD);
    }

    private static void recreateDbSchema() throws SQLException {
        executeSqlQuery("DROP SCHEMA IF EXISTS public CASCADE");
        executeSqlQuery("CREATE SCHEMA public AUTHORIZATION " + DB_USER_LOGIN);
    }

    @Before
    public void setUpDbManager() throws SQLException {
        recreateDbSchema();
        databaseManager = new DatabaseManager();
        databaseManager.connect(TEST_DB_NAME, DB_USER_LOGIN, DB_USER_PASSWORD);
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }


    @Test
    public void getTableNamesWithTwoTables() throws SQLException {
        executeSqlQuery("CREATE TABLE table1()");
        executeSqlQuery("CREATE TABLE table2()");
        String[] expectedArray = {"table1", "table2"};
        String[] actualArray = databaseManager.getTableNames();
        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    public void getTableNamesWhenDbHasNoTables() throws SQLException {
        String[] expectedArray = new String[]{};
        String[] actualArray = databaseManager.getTableNames();
        assertArrayEquals(expectedArray, actualArray);
    }

    @Test
    public void getTableDataWithEmptyTable() throws SQLException {
        executeSqlQuery("CREATE TABLE test_table()");
        DataSet[] expectedArray = new DataSet[0];
        assertArrayEquals(expectedArray, databaseManager.getTableData("test_table"));
    }

    @Test
    public void getTableDataWithNotExistingTable() throws SQLException {
        DataSet[] expectedArray = new DataSet[0];
        assertArrayEquals(expectedArray, databaseManager.getTableData("WrongTableName"));
    }

    @Test
    public void getTableDataWithValidTableTwoRows() throws SQLException {
        executeSqlQuery("CREATE TABLE test_table(" +
                "id INTEGER," +
                "name VARCHAR(128)" +
                ")");
        executeSqlQuery("INSERT INTO test_table VALUES(1,'name1')");
        executeSqlQuery("INSERT INTO test_table VALUES(2,'name2')");
        DataSet row1 = new DataSet(2);
        row1.insertValue(0, "1");
        row1.insertValue(1, "name1");
        DataSet row2 = new DataSet(2);
        row2.insertValue(0, "2");
        row2.insertValue(1, "name2");
        DataSet[] expectedArray = new DataSet[]{row1, row2};
        DataSet[] actualArray = databaseManager.getTableData("test_table");
        assertThat(actualArray, arrayContainingInAnyOrder(expectedArray));
    }

    private static void executeSqlQuery(String sqlQuery) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlQuery);
        }
    }

    @After
    public void closeDbManager() throws SQLException {
        databaseManager.close();
    }
}