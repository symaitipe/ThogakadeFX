package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class     DBConnection {
    private Connection connection;
    private static DBConnection dbConnection;
    private DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3305/ThogaKade","root","sahanSQL1234");
    }

    public static DBConnection getInstance() throws SQLException, ClassNotFoundException {
        if(dbConnection==null){
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection(){
        return connection;
    }
}
