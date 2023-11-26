import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatUser {
    private String address;
    private int port;

    public ChatUser(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void connect() throws IOException {
        Socket socket;
            socket=new Socket(address,port);
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            Thread reader = new Thread(() -> {
                String message;
                while (!Thread.interrupted()) {
                    try {
                        message = inputStream.readUTF();
                        System.out.println(message);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread writer = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                String message;
                while (!Thread.interrupted()) {
                    try {
                        message = scanner.nextLine();
                        outputStream.writeUTF(message);
                        outputStream.flush();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            reader.start();
            writer.start();

            reader.join();
            writer.join();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        boolean t=true;
        ChatUser chatUser;
        while(t){
            Scanner s=new Scanner(System.in);
            chatUser=new ChatUser(s.nextLine(),s.nextInt());
            try {
                chatUser.connect();
                t=false;
            } catch (IOException e) {
                t=true;
            }
        }
    }
}
