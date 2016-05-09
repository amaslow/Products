package desktopapplication1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Utils {

    private static Connection con;
    private static String Driver = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://172.17.18.89:3306/ELRO";
    private static String username = "root";
    private static String password = "IP1212IP";

    public static Connection getConnection() {
        try {
            Class.forName(Driver);
            con = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public static void closeDB(ResultSet rs, Statement st, Connection con) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (st != null) {
                st.close();
                st = null;
            }
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}