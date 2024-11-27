package exceptionInfo;

import java.security.Permission;

class ExceptionReporterPermission extends Permission {

    public ExceptionReporterPermission(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean implies(Permission permission) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'implies'");
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'equals'");
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hashCode'");
    }

    @Override
    public String getActions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getActions'");
    }
    // ...
}

public class ExceptionReporter {

    // Exception reporter that prints the exception
    // to the console (used as default)
    private static final Reporter PrintException = new Reporter() {
        public void report(Throwable t) {
            System.err.println(t.toString());
        }
    };

    // Stores the default reporter
    // The default reporter can be changed by the user
    private static Reporter Default = PrintException;

    // Helps change the default reporter back to
    // PrintException in the future
    public static Reporter getPrintException() {
        return PrintException;
    }

    public static Reporter getExceptionReporter() {
        return Default;
    }

    // May throw a SecurityException (which is unchecked)
    public static void setExceptionReporter(Reporter reporter) {
        // Custom permission
        ExceptionReporterPermission perm = new ExceptionReporterPermission("exc.reporter");
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // Check whether the caller has appropriate permissions
            sm.checkPermission(perm);
        }
        // Change the default exception reporter
        Default = reporter;
    }
}
