package exceptionInfo;

import javax.naming.*;

public class BadInsufficientResourcesException {

    public String input="input";
    
    public void badIRE1()throws NamingException{
        Context ctxt=new InitialContext();
        ctxt.lookup(input); //interface ezért nem dob bugot, konkrét implementációt kéne ide írni
    }

    public void badIRE2()throws SecurityIOException{
        try{
            Context ctxt=new InitialContext();
            ctxt.lookup(input);
        }
        catch(InsufficientResourcesException e){
            throw new SecurityIOException();
        }
        catch(NamingException e){

        }
    }
}
