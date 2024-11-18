package exceptionInfo;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class GoodMissingResourceException {

    private String input = "input";

    public void GoodMRE1() {
        try {
            ResourceBundle resources = ResourceBundle.getBundle("resources");
            resources.getObject(null);
        } catch (MissingResourceException e) {
            // do something
        }
    }

    public void GoodMRE2() {
        ResourceBundle resources = null;

        try {
            switch (input) {
                case "goodinput1":
                    resources = ResourceBundle.getBundle("actualpath//path/bundle1");
                    break;
                case "goodinput2":
                    resources = ResourceBundle.getBundle("actualpath//path/bundle2");
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
