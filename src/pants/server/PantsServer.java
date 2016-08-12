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
                
                //Check every tick if 'selectedClient' is still connected (this doesn't seem to be a very good idea, but it works. ok? ok.)
                if (selectedClient != null) {
                    boolean sentData = false;
                    try {
                        selectedClient.sendUrgentData(0);
                        sentData = true;
                    } catch (Exception e) {}
                    
                    if (selectedClient.isClosed() || !sentData) {
                        System.out.println(cck.get(ccv.indexOf(selectedClient)) + " disconnected!");
                        connectedClients.remove(cck.get(ccv.indexOf(selectedClient)));
                        selectedClient = null;

                        selectOtherClient();
                    }
                }
                
                try {
                    sc = cck.get(ccv.indexOf(selectedClient));
                }
                catch (Exception e) {
                    System.out.println("No clients connected");
                    // Wait 5 seconds to not spam the console
                    try {
                        Thread.sleep(5_000);
                    } catch (Exception ignored) {};
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
                } else if(input.equalsIgnoreCase("list")) {
                    //List the currently connected clients
                    System.out.println("Clients currently connected: ");
                    System.out.print("    ");
                    for (String name : cck) {
                        System.out.print(name + " ");
                    }
                    System.out.println();
                } else {
                    try {
                        new PrintStream(selectedClient.getOutputStream()).println(input);
                        Thread.sleep(2_000);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    
    private static void selectOtherClient() {
        if (!connectedClients.isEmpty()) {
            for (String name : connectedClients.keySet()) {
                Socket sock = connectedClients.get(name);
                boolean sentData = false;
                try {
                    sock.sendUrgentData(0);
                    sentData = true;
                } catch (IOException e) {}
                
                if (sock.isClosed() || !sentData) {
                    connectedClients.remove(name);
                    System.out.println(name + " disconnected!");
                    continue;
                }
                
                selectedClient = sock;
            }
        }
    }

    public static void addClient(Socket client) {
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // PrintStream toClient = new PrintStream(client.getOutputStream());
            String in = fromClient.readLine();
            
            // Use different names for multiple clients in clients (Ya 'now what i mean)
            while(connectedClients.containsKey(in)) {
                in += '_';
            }
            
            connectedClients.put(in, client);
            System.out.println(in + " connected!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
