package juja.sqlcmd;

import java.sql.SQLException;

public interface DatabaseManager {
    boolean connect(String database, String user, String password);

    void close() throws SQLException;

    String[] getTableNames() throws SQLException;

    DataSet[] getTableData(String tableName) throws SQLException;

    boolean insert(String tableName, DataSet dataset);

    boolean delete(String tableName, int id);

    boolean update(String tableName, int id);
}
