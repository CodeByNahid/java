package lab;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args){
  
   try{
        ServerSocket ss=new ServerSocket(5000);
        System.out.println("Server has been Created wt for client");
        Socket s= ss.accept();
        System.out.println("Client has been Connected");
   }
   catch(Exception e){
       System.out.println(e);
   }
    }
}
