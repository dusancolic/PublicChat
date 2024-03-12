package server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

public class ServerThread implements Runnable{

    private Socket socket;
    private List<String> usernames = Server.usernames;
    private List<String> messages = Server.messages;
    private List<ServerThread> clients = Server.clients;
    private List<String> censoredWords = Server.censoredWords;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");
    private int messageLimit = 100;
    private String username;
    private PrintWriter output;
    public ServerThread(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            )
        {
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
            String message;
            output.println("Welcome to the chat room! Please enter your username: ");

            message = input.readLine();
            while(usernameAlreadyExists(message, output)){
                message = input.readLine();
            }

            username = message;
            output.println("Welcome " + username + ", we hope you brought pizza!");
            clients.add(this);
            showHistory();
            sendMessageToEveryoneExceptSender(username + " has joined the chat room!");

            chatWithOthers(message, input);
            

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            sendMessageToEveryoneExceptSender(username + " has left the chat room!");
            clients.remove(this);
            if(output != null )output.close();
            try {
                if(socket != null)socket.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private boolean usernameAlreadyExists(String username, PrintWriter output){
        System.out.println("Checking if username already exists");
        if(usernames.contains(username)){
            output.println("Username already exists. Please enter a different username: ");
            return true;
        }
        Iterator<String> iterator = censoredWords.iterator();
        String name;
        while(iterator.hasNext())
        {
            name = iterator.next();
            if(name.equals(username)) {
                output.println("Username is banned. Please enter a different username: ");
                return true;
            }
        }
        usernames.add(username);
        return false;
    }

    private void sendMessageToEveryoneExceptSender(String message) {
        clients.iterator().forEachRemaining(client -> {
            if(client != this){
                client.getOutput().println(message);
            }
        });
    }

    private String formatMessage(String message, String username) {
        String censoredMessage = message;
        String word;
        Iterator<String> iterator = censoredWords.iterator();
        while(iterator.hasNext())
        {
            word = iterator.next();
            if(censoredMessage.contains(word))
                 censoredMessage = censoredMessage.replace(word, word.charAt(0)+"*".repeat(word.length()-2)+word.charAt(word.length()-1));
            System.out.println(word);
            System.out.println(censoredMessage);
        }
        String formattedMessage =  LocalDateTime.now().format(formatter) + " - " + username + " : " + censoredMessage;
        if(messages.size() == messageLimit)
            messages.remove(0);
        messages.add(formattedMessage);
        return formattedMessage;
    }

    private void showHistory() {
        messages.iterator().forEachRemaining(message -> {
            output.println(message);
        });
        output.println("End of chat history.");
    }
    private void chatWithOthers(String message, BufferedReader input) throws IOException {
        while(!(message = input.readLine()).equals("/leave")){
            message = formatMessage(message, username);
            sendMessageToEveryoneExceptSender(message);
            output.println(message);

            System.out.println(messages.size());
        }
        output.println("Hope to see you soon! And don't forget to bring pizza!");
        usernames.remove(username);
    }
    public PrintWriter getOutput() {
        return output;
    }
}
