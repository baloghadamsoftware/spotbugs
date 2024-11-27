package exceptionInfo;

import java.io.IOException;

public class SecurityIOException extends IOException {
    public SecurityIOException(Exception e){
        System.out.println("Hello there" + e.getMessage());
    }
};