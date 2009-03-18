package mydaogen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
  public static Connection getDbConnection() throws ClassNotFoundException, SQLException {
        Connection conn;
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smil", "widgeli", "widgeli");
        return conn;
    }
}
