package exceptionInfo;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class BadMissingResourceException {
    
    public void badMRE1()throws MissingResourceException{
        ResourceBundle resources=ResourceBundle.getBundle("resources");
        resources.getObject(null);
    }
    
    public void badMRE2() throws RuntimeException{
        try{
            ResourceBundle resources=ResourceBundle.getBundle("resources");
        }
        catch(MissingResourceException e){
            throw new RuntimeException("missing resource", e);
        }
    }

    public void badMRE3() throws SecurityIOException{
        try{
            ResourceBundle resources=ResourceBundle.getBundle("resources");
        }
        catch(MissingResourceException e){
            throw new SecurityIOException();
        }
    }
}
