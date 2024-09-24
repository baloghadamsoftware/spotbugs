package exceptionInfo;

import java.io.*;

public class GoodFileNotFoundException {

    public String input = "input";

    public void goodFNFE1() {
        File file = null;
        try {
            file = new File(System.getenv("APPDATA") +
                    input).getCanonicalFile();
            if (!file.getPath().startsWith("c:\\homepath")) {
                System.out.println("Invalid file");
                return;
            }
        } catch (IOException x) {
            System.out.println("Invalid file");
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
        } catch (FileNotFoundException x) {
            System.out.println("Invalid file");
            return;
        }
    }

    public void goodFNFE2() {
        FileInputStream fis = null;
        try {
            switch (Integer.valueOf(input)) {
                case 1:
                    fis = new FileInputStream("c:\\homepath\\file1");
                    break;
                case 2:
                    fis = new FileInputStream("c:\\homepath\\file2");
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
