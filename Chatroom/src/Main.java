import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner s=new Scanner(System.in);
    ChatRoom chatRoom=new ChatRoom(s.nextInt());
    chatRoom.startserver();

    }
}