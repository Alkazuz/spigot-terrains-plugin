package br.alkazuz.terrenos.storage;

import br.alkazuz.terrenos.Main;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteCore implements DBCore {
    private Connection connection;

    private String dbLocation;

    private String dbName;

    private File file;

    public SQLiteCore(String dbLocation) {
        this.dbName = "clans";
        this.dbLocation = dbLocation;
        initialize();
    }

    private void initialize() {
        if (this.file == null) {
            File dbFolder = new File(this.dbLocation);
            if (this.dbName.contains("/") || this.dbName.contains("\\") || this.dbName.endsWith(".db")) {
                Main.debug("The database name can not contain: /, \\, or .db");
                return;
            }
            if (!dbFolder.exists())
                dbFolder.mkdir();
            this.file = new File(dbFolder.getAbsolutePath() + File.separator + this.dbName + ".db");
        }
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.file.getAbsolutePath());
        } catch (SQLException ex) {
            Main.debug("SQLite exception on initialize " + ex);
        } catch (ClassNotFoundException ex) {
            Main.debug("You need the SQLite library " + ex);
        }
    }

    public Connection getConnection() {
        if (this.connection == null)
            initialize();
        return this.connection;
    }

    public Boolean checkConnection() {
        return Boolean.valueOf((getConnection() != null));
    }

    public PreparedStatement prepareStatement(String statement) {
        try {
            return this.connection.prepareStatement(statement);
        } catch (SQLException ex) {
            Main.debug("Error at creating the statement: " + statement + "(" + ex.getMessage() + ")");
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String statement, int paramInt) {
        try {
            return this.connection.prepareStatement(statement, paramInt);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Main.debug("Error at creating the statement: " + statement + "(" + ex.getMessage() + ")");
            return null;
        }
    }

    public void close() {
        try {
            if (this.connection != null)
                this.connection.close();
        } catch (Exception e) {
            Main.debug("Failed to close database connection! " + e.getMessage());
        }
    }

    public ResultSet select(String query) {
        try {
            return getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            Main.debug("Error at SQL Query: " + ex.getMessage());
            Main.debug("Query: " + query);
            return null;
        }
    }

    public void insert(String query) {
        try {
            getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                Main.debug("Error at SQL INSERT Query: " + ex);
                Main.debug("Query: " + query);
            }
        }
    }

    public void update(String query) {
        try {
            getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                Main.debug("Error at SQL UPDATE Query: " + ex);
                Main.debug("Query: " + query);
            }
        }
    }

    public void delete(String query) {
        try {
            getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                Main.debug("Error at SQL DELETE Query: " + ex);
                Main.debug("Query: " + query);
            }
        }
    }

    public Boolean execute(String query) {
        try {
            getConnection().createStatement().execute(query);
            return Boolean.valueOf(true);
        } catch (SQLException ex) {
            Main.debug(ex.getMessage());
            Main.debug("Query: " + query);
            return Boolean.valueOf(false);
        }
    }

    public Boolean existsTable(String table) {
        try {
            ResultSet tables = getConnection().getMetaData().getTables(null, null, table, null);
            return Boolean.valueOf(tables.next());
        } catch (SQLException e) {
            Main.debug("Failed to check if table '" + table + "' exists: " + e.getMessage());
            return Boolean.valueOf(false);
        }
    }

    public Boolean existsColumn(String tabell, String colum) {
        try {
            ResultSet colums = getConnection().getMetaData().getColumns(null, null, tabell, colum);
            return Boolean.valueOf(colums.next());
        } catch (SQLException e) {
            Main.debug("Failed to check if colum '" + colum + "' exists: " + e.getMessage());
            return Boolean.valueOf(false);
        }
    }
}
