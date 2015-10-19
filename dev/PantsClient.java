// This file is pseudocode!

public class PantsClient:

    @Getter @Setter static Socket client = null;

    public static void onLoad(): // Call from JavaPlugin
        Thread checkConnectionThread = new Thread(new CheckConnectionRunnable("localhost", 444));
        checkConnectionThread.start();

    public static void handshake():
        //Do stuff with readers in here or whatever

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
