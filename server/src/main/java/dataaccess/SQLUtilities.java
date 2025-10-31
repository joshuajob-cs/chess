package dataaccess;

import model.SQLData;

import java.sql.SQLException;

public class SQLUtilities {
    static void SQLClear(String table) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE TABLE " + table + ";")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean exists(String value, String col, String table) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "SELECT " + col +
                            " FROM " + table +
                            " WHERE " + col +
                            " = ?;")) {
                statement.setString(1, value);
                var matches = statement.executeQuery();
                return matches.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void insert(SQLData data, String table){
        try(var conn = DatabaseManager.getConnection()) {
            var translation = data.toSQL();
            String placeholders = "(";
            for(int i = 0; i < translation.size(); i++){
                placeholders += "?, ";
            }
            placeholders = placeholders.substring(0, placeholders.length() - 2) + ")";
            try(var statement = conn.prepareStatement(
                    "INSERT INTO " + table +
                            " VALUES " + placeholders + ";")){
                for (int i = 0; i < translation.size(); i++){
                    statement.setString(i + 1, translation.get(i));
                }
                statement.executeUpdate();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    static public <T> void lookUp(Class<T> type, String key, String keyCol, String table) {
    }
}