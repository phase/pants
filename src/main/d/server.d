import std.stdio;
import std.socket;

void main() {
    auto listener = new Socket(AddressFamily.INET, SocketType.STREAM);
    listener.bind(new InternetAddress("localhost", 444));
    listener.listen(10);
    auto readSet = new SocketSet();

    string[Socket] connectedClients;
    Socket selectedClient;
    char[1024] buffer;
    bool isRunning = true;

    while(isRunning) {
        readSet.reset();
        readSet.add(listener);

        foreach(client; connectedClients) readSet.add(client);

        if(Socket.select(readSet, null, null)) {
            foreach(client; connectedClients.keys)
                if(readSet.isSet(client)) {
                    string ip = connectedClients[client];

                    // Read from it and echo it back
                    auto got = client.receive(buffer);
                    string out = "[" ~ ip ~ "] " ~ buffer[0..got];
                    writeln(out);
                }
            if(readSet.isSet(listener)) {
                // The listener is ready to read, which means
                // a new client wants to connect. We accept it here.
                auto client = listener.accept();

                auto got = client.recieve(buffer); // Get Ip
                string ip = buffer[0..got];
                string out = ip ~ " connected!";
                writeln(out);

                connectedClients[client] = ip; // Add to the associative array
                if(selectedClient is null) {
                    selectedClient = client;
                }
            }
        }
    }
}
