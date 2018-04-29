package juja.sqlcmd;

import juja.sqlcmd.controller.MainController;
import juja.sqlcmd.view.Console;
import juja.sqlcmd.view.View;

public class Main {
    public static void main(String[] args) {
        View view = new Console();
        DatabaseManager databaseManager = new JdbcDatabaseManager();
        MainController controller = new MainController(view, databaseManager);
        controller.run();
    }
}