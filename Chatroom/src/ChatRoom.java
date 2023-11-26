import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ChatRoom {
    private int port;
    private ArrayList<Socket> listofusers;
    private ArrayList<String> listnames;

    public ChatRoom(int port) {
        this.port = port;
    }
    public void usercount(ArrayList<Socket> listofusers) throws IOException {
        for(int i=0; i<listofusers.size(); i++){
            DataOutputStream dataOutputStream=null;
            dataOutputStream=new DataOutputStream(listofusers.get(i).getOutputStream());
            dataOutputStream.writeUTF("Current Users: "+listofusers.size());
            dataOutputStream.flush();
        }
    }
    public void userconnection(Socket socket1){
        Socket socket=socket1;
        Thread socketconn=new Thread(()->{
            boolean userstatus=true;
            DataInputStream inputStream = null;
            DataOutputStream outputStream = null;
            String message="";
            try {
                while(userstatus){
                    System.out.println("thredis whileshia");
                    inputStream = new DataInputStream(socket.getInputStream());
                    String temp=inputStream.readUTF();
                    if(temp.substring(0,temp.indexOf("-")+1).equals("/server changename-")){
                        System.out.println("changenameze movida?");
                        String newstatus=temp.substring(19);
                        for(int i=0; i<listofusers.size(); i++){
                            if(socket==listofusers.get(i)){
                                listnames.set(i,newstatus);
                            }
                        }
                    }else if(temp.substring(0,temp.indexOf("-")+1).equals("/server dm-")){
                        System.out.println("Server dmze movida?");
                        String dmname=temp.substring(temp.indexOf('-')+1,temp.indexOf(':'));//(USERNAME)
                        String message2=temp.substring(temp.indexOf(':'));//(message)
                        String transmitterusername="";
                        for(int k=0; k<listnames.size(); k++){
                            if(listofusers.get(k)==socket){
                                System.out.println("ifshi movida");
                                transmitterusername=listnames.get(k);
                            }
                            if(listnames.get(k).equals(dmname)){
                                outputStream=new DataOutputStream(listofusers.get(k).getOutputStream());//outputstream for receiveruser
                                outputStream.writeUTF(transmitterusername+message2);
                            }
                        }
                    }else if(temp.equals("/server exit")){
                    userstatus=false;
                        for(int i=0; i<listofusers.size(); i++){
                            if(listofusers.get(i)==socket) {
                                System.out.println("daaremova");
                                listofusers.remove(i);
                                listnames.remove(i);
                                try {
                                    usercount(listofusers);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }
                    }else if(!temp.equals("")){
                        System.out.println(" mesijebis ifshi movida");
                        String name="";
                        for(int f=0; f<listnames.size(); f++){
                            if(listofusers.get(f)==socket){
                                name=listnames.get(f);
                            }
                        }
                        message = name+": "+temp;
                        for(int j=0; j<listofusers.size(); j++){
                            if(listofusers.size()>1){
                                Socket s1=listofusers.get(j);
                                if(socket!=s1){
                                    outputStream = new DataOutputStream(s1.getOutputStream());
                                    outputStream.writeUTF(message);
                                    outputStream.flush();
                                }
                            }else{
                                System.out.println("elseshi movida");;
                                break;
                            }
                        }
                    }
                }
            }
            catch (IOException e) {
                for(int i=0; i<listofusers.size(); i++){
                    if(listofusers.get(i)==socket) {
                        System.out.println("daaremova");
                        listofusers.remove(i);
                        listnames.remove(i);
                        try {
                            usercount(listofusers);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                            }
                        }
                    }
                userstatus=false;
            }
        });
        System.out.println("startamde modis");
        socketconn.start();
    }
    public void startserver(){
        listofusers=new ArrayList<>();
        listnames=new ArrayList<>();
        int n=0;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket = null;
            while (true) {
                System.out.println("mainis whileshia");
                try {
                    n++;
                    System.out.println("waiting for client"+"\n");
                    socket = serverSocket.accept();
                    System.out.println("found client"+"\n");
                    DataOutputStream outputStream2=new DataOutputStream(socket.getOutputStream());
                    outputStream2.writeUTF("Welcome to ChatRoom"+"\n"+"Commands:"+"\n"+"/server changename-(NEWNAME)"+"\n"+"/server dm-(USERNAME):(message)"+"\n"+"/server exit");
                    outputStream2.flush();
                    listofusers.add(socket);
                    listnames.add("anonymous"+n);
                    usercount(listofusers);
                    userconnection(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
