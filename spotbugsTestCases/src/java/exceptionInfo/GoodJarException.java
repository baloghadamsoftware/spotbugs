package exceptionInfo;

import java.io.IOException;
import java.util.jar.*;

public class GoodJarException {
    private String input = "input";

    public void GoodJE1() {
        try {
            JarFile jf = new JarFile(input);
        } catch (JarException e) {
            System.out.println("connection failed");
        } catch (IOException e) {
            System.out.println("connection failed");
        }
    }

    public void GoodJE2() {
        JarFile jf = null;
        try {
            switch (input) {
                case "goodinput1":
                    jf = new JarFile("c:\\homepath\\file1");
                    break;
                case "goodinput2":
                    jf = new JarFile("c:\\homepath\\file2");
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
