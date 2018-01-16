package juja.sqlcmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final String jdbcDriverClass;
    private final String jdbcUrl;
    private Connection connection;

    public DatabaseManager() {
        jdbcDriverClass = "org.postgresql.Driver";
        jdbcUrl = "jdbc:postgresql://localhost:5432/";
    }

    DatabaseManager(String jdbcDriverClass, String jdbcUrl) {
        this.jdbcDriverClass = jdbcDriverClass;
        this.jdbcUrl = jdbcUrl;
    }

    public boolean connect(String database, String user, String password) {
        try {
            Class.forName(jdbcDriverClass);
            connection = DriverManager.getConnection(
                    jdbcUrl + database, user, password);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public String[] getTableNames() throws SQLException {
        try (ResultSet rs = connection.getMetaData().getTables(
                null, "public", "%", new String[]{"TABLE"})) {
            int tablesCount = 0;
            if (rs.last()) {
                tablesCount = rs.getRow();
                rs.beforeFirst();
            }
            String[] tableNames = new String[tablesCount];
            int index = 0;
            while (rs.next()) {
                tableNames[index++] = rs.getString("table_name");
            }
            return tableNames;
        }
    }

    public DataSet[] getTableData(String tableName) throws SQLException {
        if (!isTableExists(tableName)) {
            return new DataSet[0];
        }
        int tableSize = tableSize(tableName);
        if (tableSize == 0) {
            return new DataSet[0];
        }
        DataSet[] dataSets = new DataSet[tableSize];
        int numberOfColumns = numberOfColumns(tableName);
        fillDataSets(tableName, dataSets, numberOfColumns);
        return dataSets;
    }

    private boolean isTableExists(String tableName) throws SQLException {
        for (String name : getTableNames()) {
            if (name.equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    private void fillDataSets(String tableName, DataSet[] result, int numberOfColumns) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM public." + tableName)) {
            int index = 0;
            while (rs.next()) {
                DataSet dataSet = new DataSet(numberOfColumns);
                result[index++] = dataSet;
                for (int i = 0; i < numberOfColumns; i++) {
                    dataSet.insertValue(i, rs.getString(i + 1));
                }
            }
        }
    }

    private int numberOfColumns(String tableName) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            return rsmd.getColumnCount();
        }
    }

    private int tableSize(String tableName) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT COUNT(*) as RECORDS FROM " + tableName)) {
            if (resultSet.next())
                return resultSet.getInt("RECORDS");
            else return 0;
        }
    }

}
