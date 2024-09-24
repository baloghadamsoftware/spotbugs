package exceptionInfo;

import java.util.logging.*;

class MyExceptionReporter extends ExceptionReporter {
    private static final Logger logger = Logger.getLogger("com.organization.Log");

    public static void report(Throwable t) {
        t = filter(t);
        if (t != null) {
            logger.log(Level.FINEST, "Loggable exception occurred", t);
        }
    }

    public static Exception filter(Throwable t) {
        if (t instanceof SensitiveException1) {
            // Too sensitive, return nothing (so that no logging happens)
            return null;
        } else if (t instanceof SensitiveException2) {
            // Return a default insensitive exception instead
            return new FilteredSensitiveException(t);
        }
        // ...
        // Return for reporting to the user
        return new Exception(t);    
    }
}

class SensitiveException1 extends Exception{

}

class SensitiveException2 extends Exception{

}

class FilteredSensitiveException extends Exception {
    public FilteredSensitiveException(Throwable e){

    }

}