package pants.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class PantsServer {

    static ServerSocket server;
    static HashMap<String, Socket> connectedClients = new HashMap<>();
    static Socket selectedClient = null;

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Pants Server");
        server = new ServerSocket(444);
        new Thread(() -> {
            while (true) {
                try {
                    Socket client = server.accept();
                    addClient(client);
                    if (selectedClient == null) selectedClient = client;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void addClient(Socket client) {
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintStream toClient = new PrintStream(client.getOutputStream());
            String in = fromClient.readLine();
            System.out.println(in + " connected!");
            new Thread(() -> {
                while (true)
                    try {
                        System.out.println(fromClient.readLine());
                    }
                    catch (Exception e) {}
            }).start();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Please input command to send: ");
                String input = scanner.nextLine();
                if (input.isEmpty()) {
                    scanner.close();
                    break;
                }
                toClient.println(input);
                Thread.sleep(10000);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
