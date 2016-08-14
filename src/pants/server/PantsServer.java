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
                
                // Ping the clients to check if any is disconnected,
                //  and if the selected client is disconnected,
                //  select another client
                //!!!DO THIS IN THIS THREAD!
                //!!!DUE TO A SOMETHING STRANGE, ALL THE CLIENTS
                //!!!WILL DISCONNECT IF THIS IS DONE IN ANOTHER THREAD
                if (selectedClient != null && !connectedClients.isEmpty()) {
                    try {
                        pingClients(false);
                    } catch (ConcurrentModificationException e) {
                        //Probably a client/the selectedClient
                        //disconnected while pinging.
                        //Trying again and selecting another client
                        pingClients(true);
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
                    for (String name : connectedClients.keySet()) {
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

    private static void pingClients(boolean tryToSelectOther) {
        //This variable name seems a bit too long
        boolean triedToSelectOtherClientFromStart = tryToSelectOther;
        
        for (String name : connectedClients.keySet()) {
            Socket sock = connectedClients.get(name);
            if(sock == null) {
                connectedClients.remove(name);
                continue;
            }
            
            boolean connected = ping(sock);
            if (!connected) {
                try {
                    sock.close();
                } catch (IOException e) {
                    //Ignore any kind of exception thrown here
                }
                connectedClients.remove(name);
                System.out.println(name + " disconnected!");
                if (sock == selectedClient) {
                    selectedClient = null;
                    tryToSelectOther = true;
                }
            } else if (tryToSelectOther) {
                selectedClient = sock;
                tryToSelectOther = false;
            }
        }

        if (tryToSelectOther && !triedToSelectOtherClientFromStart)
            pingClients(tryToSelectOther);
    }
    
    private static boolean ping(Socket sock) {
        boolean sentData = false;
        
        try {
            /* Send an 'urgent byte' to the client. If not arrived,
             * throws an exception and sentData will not be set,
             * if arrived, sentData will be set, 
             * and it will be silently discarded by the client
             */
            sock.sendUrgentData(0);
            sentData = true;
        } catch (IOException e) {}
        
        //If we cannot send commands to the client, it's now useless.
        return sentData && !sock.isClosed() && !sock.isOutputShutdown();
    }
    
    public static void addClient(Socket client) {
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
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
