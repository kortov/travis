package juja.sqlcmd;

import java.util.Arrays;

public class DataSet {
    private final String[] row;
    private final int rowSize;

    public DataSet(int rowSize) {
        this.rowSize = rowSize;
        row = new String[rowSize];
    }


    public void insertValue(int columnIndex, String string) {
        if (columnIndex >= rowSize || columnIndex < 0) {
            String message = String.format("Column index should be between 0 and %s-1", rowSize);
            throw new IllegalArgumentException(message);
        }
        row[columnIndex] = string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSet dataSet = (DataSet) o;
        return rowSize == dataSet.rowSize && Arrays.equals(row, dataSet.row);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(row);
        result = 31 * result + rowSize;
        return result;
    }
}
