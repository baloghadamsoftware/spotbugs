package exceptionInfo;

import java.net.BindException;
import java.net.SocketAddress;

import java.io.*;
import java.net.*;

public class BadBindException {

    public void badBindException1() throws IOException {
        String ip="1";
        int port=1;
        Socket socket = new Socket(ip, port);
    }

    public void badBindException2() throws IOException {
        String ip="1";
        int port=1;
        try{
            Socket socket=new Socket(ip,port);
        }
        catch(BindException e){
            throw new IOException("BindException", e);
        }
        catch(IOException e){

        }
    }

    public void badBindException3() throws IOException {
        String ip="1";
        int port=1;
        try{
            Socket socket=new Socket(ip,port);
        }
        catch(BindException e){
            throw new IOException("BindException", e);
        }
        catch(UnknownHostException e){

        }
        catch(IllegalArgumentException e){

        }
        catch(IOException e){

        }
    }
    public void badBindException4() throws SecurityIOException {
        String ip="1";
        int port=1;
        try{
            Socket socket=new Socket(ip,port);
        }
        catch(BindException e){
            throw new SecurityIOException();
        }
        catch(IOException e){

        }
    }
}
