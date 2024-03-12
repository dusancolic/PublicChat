package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server{

    public static final int PORT = 9001;
    public static List<String> messages = new CopyOnWriteArrayList<>();
    public static List<ServerThread> clients = new CopyOnWriteArrayList<>();
    public static List<String> censoredWords = new CopyOnWriteArrayList<>();
    public static List<String> usernames = new CopyOnWriteArrayList<>();

    public Server(){
        try {
            addCensoredWords();
            ServerSocket serverSocket = new ServerSocket(PORT);
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
          new Server();
    }

    private void addCensoredWords(){
        censoredWords.add("aaaa");
        censoredWords.add("bbbb");
        censoredWords.add("cccc");
        censoredWords.add("dddd");
    }
}
