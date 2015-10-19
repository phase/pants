// This file is pseudocode!

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
