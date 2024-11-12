package exceptionInfo;

import java.io.*;
import java.net.*;

public class BadBindException {

    public void badBindException1() throws IOException {
        String ip="1";
        int port=1;
        ServerSocket socket = new ServerSocket(1);
        socket.bind(new InetSocketAddress(port));
    }

    public void badBindException2() throws IOException {
        String ip="1";
        int port=1;
        try{
            ServerSocket socket=new ServerSocket(port);
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
            ServerSocket socket=new ServerSocket(port);
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
            ServerSocket socket=new ServerSocket(port);
        }
        catch(BindException e){
            throw new SecurityIOException();
        }
        catch(IOException e){

        }
    }
}
