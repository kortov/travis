package juja.sqlcmd.view;

public interface View {
    void write(String message);

    String read();
}