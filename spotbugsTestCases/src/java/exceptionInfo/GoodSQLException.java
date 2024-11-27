package exceptionInfo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GoodSQLException {

    public String input1 = "input1";
    public String input2 = "input2";
    public String input3 = "input3";

    // you gotta finish this one
    public void goodSQLE1() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(input1, input2, input3);
            if (!conn.getMetaData().getURL().startsWith("homepath")) {
                System.out.println("Invalid Credentials");
                return;
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Can't Load Driver");
            return;
        } catch (SQLException e) {
            System.out.println("Invalid Credentials");
            return;
        }
    }

    public void goodSQLE2() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            switch (Integer.valueOf(input1)) {
                case 1:
                    conn = DriverManager.getConnection("homepath.db1",input2,input3);
                    break;
                case 2:
                conn = DriverManager.getConnection("homepath.db2",input2,input3);
                break;
                // ...
                default:
                    System.out.println("Invalid option");
                    break;
            }
        } catch (Throwable t) {
            MyExceptionReporter.report(t); // Sanitize
        }
    }
}
