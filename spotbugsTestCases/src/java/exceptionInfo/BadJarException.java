package exceptionInfo;

import java.io.File;
import java.io.IOException;
import java.util.jar.*;

public class BadJarException {
    public File file=new File("test.txt");
    public String input="input.txt";

    public void badJE1() throws IOException{
        JarFile jf=new JarFile(file);
    }

    public void badJE2() throws IOException{
        JarFile jf=new JarFile(input);
    }

    public void badJE12() throws IOException{
        try{
            JarFile jf=new JarFile(file);
        }
        catch(JarException e){
            throw new IOException("bad jarfile",e);
        }
    }

    public void badJE22() throws IOException{
        try{
            JarFile jf=new JarFile(input);
        }
        catch(JarException e){
            throw new IOException("bad jarfile",e);
        }
    }

    public void badJE13() throws SecurityIOException{
        try{
            JarFile jf=new JarFile(file);
        }
        catch(JarException e){
            throw new SecurityIOException();
        }
        catch(IOException e){

        }
    }

    public void badJE23() throws SecurityIOException{
        try{
            JarFile jf=new JarFile(input);
        }
        catch(JarException e){
            throw new SecurityIOException();
        }
        catch(IOException e){

        }
    }
}
