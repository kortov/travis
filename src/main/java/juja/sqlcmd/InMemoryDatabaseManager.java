package juja.sqlcmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDatabaseManager implements DatabaseManager {
    private Map<String, Table> tables;

    public InMemoryDatabaseManager() {
        this.tables = new HashMap<>();
    }

    public void createTable(String tableName, int columnsNumber) {
        if (tableName == null) {
            throw new IllegalArgumentException("Table name must not be null!");
        }
        if (columnsNumber < 0) {
            throw new IllegalArgumentException("Number of columns must be positive!");
        }
        tables.put(tableName, new Table(columnsNumber));
    }

    @Override
    public boolean connect(String database, String user, String password) {
        return true;
    }

    @Override
    public void close() {
        //No operation
    }

    @Override
    public String[] getTableNames() {
        return tables.keySet().toArray(new String[0]);
    }

    @Override
    public DataSet[] getTableData(String tableName) {
        Table table = tables.get(tableName);
        return table != null ? table.tableData() : new DataSet[0];
    }

    @Override
    public boolean insert(String tableName, DataSet dataset) {
        Table table = tables.get(tableName);
        return table != null && table.insert(dataset);
    }

    @Override
    public boolean delete(String tableName, int id) {
        Table table = tables.get(tableName);
        return table != null && table.delete(id);
    }

    @Override
    public boolean update(String tableName, int id) {
        throw new UnsupportedOperationException();
    }

    private static class Table {
        private List<DataSet> rows;
        private int columnsNumber;

        Table(int columnsNumber) {
            this.columnsNumber = columnsNumber;
            rows = new ArrayList<>();
        }

        DataSet[] tableData() {
            return rows.toArray(new DataSet[0]);
        }

        boolean insert(DataSet row) {
            if (row.getRowSize() != columnsNumber) {
                return false;
            }
            if (hasRowNullElement(row)) {
                return false;
            }
            rows.add(row);
            return true;
        }

        boolean delete(int id) {
            int rowIndexWithId = findRowById(id);
            if (rowIndexWithId != -1) {
                rows.remove(rowIndexWithId);
                return true;
            }
            return false;
        }

        private int findRowById(int id) {
            for (int index = 0; index < rows.size(); index++) {
                DataSet currentRow = rows.get(index);
                String currentRowId = currentRow.values()[0];
                if (currentRowId.equals(String.valueOf(id))) {
                    return index;
                }
            }
            return -1;
        }

        private boolean hasRowNullElement(DataSet row) {
            for (String s : row.values()) {
                if (s == null) {
                    return true;
                }
            }
            return false;
        }
    }
}
