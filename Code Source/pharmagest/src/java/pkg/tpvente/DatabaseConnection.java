package pkg.tpvente;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DATABASE_NAME = "liwellabdou_pharmagest";
    private static final String DATABASE_USER = "liwellabdou";
    private static final String DATABASE_PASSWORD = "Exelin38@";
    private static final String URL = "jdbc:postgresql://postgresql-liwellabdou.alwaysdata.net:5432/liwellabdou_pharmagest";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erreur de chargement du driver PostgreSQL", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DATABASE_USER, DATABASE_PASSWORD);
    }
}
