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
        Scanner scanner = new Scanner(System.in);
        new Thread(() -> {
            System.out.println("Starting sending inputs");
            while (true) {
                ArrayList<String> cck = new ArrayList<String>(connectedClients.keySet());
                ArrayList<Socket> ccv = new ArrayList<Socket>(connectedClients.values());
                String sc = "";
                try {
                    sc = cck.get(ccv.indexOf(selectedClient));
                }
                catch (Exception e) {
                    System.out.println("No clients connected");
                    continue;
                }
                System.out.println("Please input command to send to " + sc + ": ");
                String input = scanner.nextLine();
                if (input.startsWith("select ")) {
                    String is = input.split("select ")[1];
                    if (connectedClients.keySet().contains(is)) {
                        selectedClient = connectedClients.get(is);
                    }
                    else {
                        System.out.println(is + " is not connected!");
                    }
                }
                else {
                    try {
                        new PrintStream(selectedClient.getOutputStream()).println(input);
                        Thread.sleep(10000);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void addClient(Socket client) {
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // PrintStream toClient = new PrintStream(client.getOutputStream());
            String in = fromClient.readLine();
            System.out.println(in + " connected!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
