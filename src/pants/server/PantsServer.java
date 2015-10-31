package pants.server;

import java.io.*;
import java.net.*;
import java.util.*;

/*
public class PantsServer:

    final static ServerSocket server = new ServerSocket(444);
    static HashMap<String, Socket> connectedClients = new HashMap<>();
    static Socket selectedClient = null;

    public static void main(...):
        new Thread(new Runnable():
            public void run():
                while(true):
                    Socket client = server.accept();
                    addClient(client);
                    if(selectedClient == null) selectedClient = client;
        ).start();

    public staic void addClient(Socket client):
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintStream toClient = new PrintStream(client.getOutputStream());
        String in = fromClient.readline();
        if(in.startsWith("cmd::")) runCommand(client, in.replaceFirst("cmd::", ""));
            
    public static void runCommand(Socket client, String cmd):
        if(cmd.startsWith("set ")):
            connectedClients.put(cmd.replaceFirst("set ", ""), client);

 */
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
            while (true) {
                System.out.println("Saying hello...");
                toClient.print("sudo say hello");
                Thread.sleep(10000);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
