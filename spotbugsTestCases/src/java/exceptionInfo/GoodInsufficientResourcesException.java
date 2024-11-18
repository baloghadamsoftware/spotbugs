package exceptionInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.BindException;
import javax.naming.*;

public class GoodInsufficientResourcesException {

    public String input="input";

    public void GoodIRE1(){
        try{
            Context ctxt=new MockContext();
            ctxt.lookup(input);
        }
        catch(NamingException e){
            System.out.println("connection failed");
        }
    }
    public void GoodIRE2(){
        Context ctxt=new MockContext();

        try {
            switch (input) {
                case "goodinput1":
                    ctxt.lookup("c:\\homepath\\file1");
                    break;
                case "goodinput2":
                    ctxt.lookup("c:\\homepath\\file2");
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
