
package lab;

import java.net.Socket;

public class client {
    public static void main(String[] args) {
        try{
        Socket s=new Socket("localhost",5000);
        
        }
        catch(Exception e){
            System.out.println(e);}
    }
}
