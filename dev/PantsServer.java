// This file is pseudocode!

public class PantsServer:

    final static ServerSocket server = new ServerSocket(444);
    static ArrayList<Socket> connectedClients = new ArrayList<>();

    public static void main(...):
        while(true):
            Socket client = server.accept();
            connectedClients.add(client);
            DataOutputStream outToClient = new DataOutputStream(server.getOutputStream());
            outToClient.writeBytes("Connected!\n");
