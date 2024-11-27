package exceptionInfo;

import java.io.*;
import java.net.*;

public class GoodBindException {

    String ip="1";
    int port=1;

    public void goodBE1(){

        try{
            Socket socket=new Socket(ip,port);
        }
        catch(BindException e){
            System.out.println("connection failed");
        }
        catch(IOException e){
            System.out.println("connection failed");
        }
    }

    public void goodBE2(){
        try{
            Socket socket=null;
            int offset=6;
            switch(port){
                case 1:
                    socket=new Socket(ip,offset+port);
                    break;
                case 2:
                    socket=new Socket(ip,offset+port);
                //...
                default:
                System.out.println("invalid option");
            }
        }
        catch (Throwable t) {
            MyExceptionReporter.report(t); // Sanitize
          }
    }
}
