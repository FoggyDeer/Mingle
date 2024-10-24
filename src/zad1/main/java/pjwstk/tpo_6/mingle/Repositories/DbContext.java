package pjwstk.tpo_6.mingle.Repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Driver;

public class DbContext {
    private Connection connection;
    private String connectionString = "jdbc:sqlserver://localhost:1433;DatabaseName=Mingle;";
    private String userName = "sa";
    private String password = "Zellamichael2004";

    private DbContext() throws SQLException, ClassNotFoundException {
        try {
            Class<?> driver_class = Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Driver driver = (Driver) driver_class.newInstance();
            DriverManager.registerDriver(driver);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new ClassNotFoundException("Driver class not found");
        }
        this.connection = DriverManager.getConnection(connectionString, userName, password);
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        return new DbContext().connection;
    }

}
