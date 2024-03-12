package client;

import java.io.*;
import java.net.Socket;

public class Client {

    public static final int PORT = 9001;

    public Client() {

        try (
                Socket socket = new Socket("localhost", PORT);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
                )
        {
            enterUsername(input, keyboard, output);
            showHistory(input);

            Thread reader = new Thread(() -> {
                try {
                    receiveMessages(input);
                } catch (IOException e) {
                    System.out.println("Reading from server failed.");
                    System.exit(0);
                }
            });

            reader.start();
            Thread writer = new Thread(() -> {
                try {
                    chat(keyboard, output);

                } catch (IOException e) {
                    System.out.println("Writing from server failed.");
                    System.exit(0);
                }
            });

            writer.start();
            reader.join();
            writer.join();

        } catch (IOException | InterruptedException e) {
            System.out.println("Server is not running.");
        }

    }

    public static void main(String[] args) {
        new Client();
    }

    private void chat(BufferedReader keyboard, PrintWriter output) throws IOException {
        String message;
        while(!(message = keyboard.readLine()).equals("/leave")){
            output.println(message);
        }
        output.println(message);
    }

    private void receiveMessages(BufferedReader input) throws IOException {
        String line;
        while((line = input.readLine()) != null && !line.equals("Hope to see you soon! And don't forget to bring pizza!")){
            System.out.println(line);
        }
        System.out.println(line);
    }
    private void enterUsername(BufferedReader input, BufferedReader keyboard, PrintWriter output) throws IOException {
        System.out.println(input.readLine());
        String username = keyboard.readLine();
        output.println(username);
        String line;
        while((line = input.readLine()).equals("Username already exists. Please enter a different username: ") || line.equals("Username is banned. Please enter a different username: ")){
            System.out.println(line);
            username = keyboard.readLine();
            output.println(username);
        }
        System.out.println(line);
    }
    private void showHistory(BufferedReader input) throws IOException {
        String line;
        while(!(line = input.readLine()).equals("End of chat history.")){
            System.out.println(line);
        }
    }
}