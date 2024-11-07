package exceptionInfo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BadSQLException {

    public String input1="input1";
    public String input2="input2";
    public String input3="input3";

    public void badSQLE1() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(input1, input2, input3);
            DriverManager.getDriver("com.mysql.cj.jdbc.Driver");
            DriverManager.registerDriver(null);
            DriverManager.deregisterDriver(null);
        } catch (ClassNotFoundException e) {

        }
    }

    public void badSQLE2() throws IOException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(input1, input2, input3);
        } catch (ClassNotFoundException | SQLException e) {
            throw new IOException("Unable to retrieve file", e);
        }
    }

    public void badSQLE3() throws IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(input1, input2, input3);
        } catch (ClassNotFoundException | SQLException e) {
            throw new SecurityIOException();
        }
    }

}
