package dataaccess;

import java.sql.SQLException;

public class SQLUtilities {
    static public void SQLClear(String table) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE TABLE " + table + ";")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static public boolean exists(String value, String col, String table) {
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

    static public <T> void lookUp(Class<T> type, String key, String keyCol, String table) {
    }
}