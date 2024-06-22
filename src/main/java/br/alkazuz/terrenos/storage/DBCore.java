package br.alkazuz.terrenos.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public interface DBCore {
    Connection getConnection();

    Boolean checkConnection();

    void close();

    ResultSet select(String paramString);

    void insert(String paramString);

    void update(String paramString);

    void delete(String paramString);

    Boolean execute(String paramString);

    Boolean existsTable(String paramString);

    PreparedStatement prepareStatement(String paramString);

    PreparedStatement prepareStatement(String paramString, int paramInt);

    Boolean existsColumn(String paramString1, String paramString2);
}
