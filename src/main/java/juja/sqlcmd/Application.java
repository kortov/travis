package juja.sqlcmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {

    private static final String JDBC_POSTGRESQL_DRIVER = "org.postgresql.Driver";
    private static final String JDBC_PROTOCOL = "jdbc:postgresql://";
    private static final String SQL_URL = "localhost:5432/";
    private static final String DATABASE_NAME = "sqlcmd";
    private static final String USER_NAME = "sqlcmd";
    private static final String USER_PASSWORD = "sqlcmd";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String USER_TABLE_NAME = "\"user\"";
    private Connection connection = null;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        new Application().simpleSQL();
    }

    public void simpleSQL() throws SQLException, ClassNotFoundException {
        connection = getConnection();

        dropTableIfExists(USER_TABLE_NAME);
        printTableList();

        createTableUser();
        printTable(USER_TABLE_NAME);
        printTableList();

        insertIntoUserTable("user1", "password1");
        insertIntoUserTable("user2", "password2");
        insertIntoUserTable("user3", "password3");
        printTable(USER_TABLE_NAME);

        changePasswordInUserTable("user1", "password2");
        printTable(USER_TABLE_NAME);

        removeFromUserTable("user3");
        printTable(USER_TABLE_NAME);

        if (connection != null) {
            connection.close();
        }
    }

    private void dropTableIfExists(String tableName) throws SQLException {
        String sqlQuery = "DROP TABLE IF EXISTS " + tableName;
        executeUpdateQuery(sqlQuery);
    }

    private void changePasswordInUserTable(String name, String password) throws SQLException {
        String sqlQuery = String.format("UPDATE " + USER_TABLE_NAME + " SET password = '%s'" + " WHERE name='%s'", password, name);
        executeUpdateQuery(sqlQuery);
    }

    private void removeFromUserTable(String name) throws SQLException {
        String sqlQuery = String.format("DELETE FROM " + USER_TABLE_NAME + " WHERE name='%s'", name);
        executeUpdateQuery(sqlQuery);
    }

    private void printTable(String tableName) throws SQLException {
        String sqlQuery = String.format("SELECT * FROM %s order by %<s.id", USER_TABLE_NAME);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {

            final boolean isTableContainsData = resultSet.isBeforeFirst();
            if (!isTableContainsData) {
                System.out.println("table is empty" + LINE_SEPARATOR);
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();
            int columnsCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                String columnsSeparator = " |";
                for (int i = 1; i <= columnsCount; i++) {
                    stringBuilder.append(resultSet.getString(i));
                    stringBuilder.append(columnsSeparator);
                }
                stringBuilder.delete(stringBuilder.length() - columnsSeparator.length(), stringBuilder.length());
                stringBuilder.append(LINE_SEPARATOR);
            }
            System.out.println(stringBuilder);
        }
    }

    private void insertIntoUserTable(String name, String password) throws SQLException {
        String sqlQuery = String.format("INSERT INTO " + USER_TABLE_NAME + "(name, password) VALUES('%s','%s') ", name, password);
        executeUpdateQuery(sqlQuery);
    }

    private int executeUpdateQuery(String sqlQuery) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sqlQuery);
        }
    }

    private void createTableUser() throws SQLException {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME +
                "(" +
                "id SERIAL PRIMARY KEY," +
                "name text," +
                "password text" +
                ")";
        executeUpdateQuery(sqlQuery);
    }

    private void printTableList() throws SQLException {
        try (ResultSet rs = connection.getMetaData().getTables(null, "public", "%", new String[]{"TABLE"})) {
            final boolean isDbContainsTables = rs.isBeforeFirst();
            if (isDbContainsTables) {
                while (rs.next()) {
                    System.out.println(rs.getString(3));
                }
                System.out.println();
            } else {
                System.out.println("db is empty" + LINE_SEPARATOR);
            }
        }
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null) {
            Class.forName(JDBC_POSTGRESQL_DRIVER);
            return DriverManager.getConnection(
                    JDBC_PROTOCOL + SQL_URL + DATABASE_NAME, USER_NAME,
                    USER_PASSWORD);
        } else {
            return connection;
        }
    }
}
