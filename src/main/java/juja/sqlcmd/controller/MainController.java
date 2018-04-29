package juja.sqlcmd.controller;

import juja.sqlcmd.DatabaseManager;
import juja.sqlcmd.view.View;

public class MainController {
    private View view;
    private DatabaseManager databaseManager;

    public MainController(View view, DatabaseManager databaseManager) {
        this.view = view;
        this.databaseManager = databaseManager;
    }

    public void run() {

    }
}