import std.stdio;
import std.socket;

void main() {
    auto listener = new Socket(AddressFamily.INET, SocketType.STREAM);
    listener.bind(new InternetAddress("localhost", 444));
    listener.listen(10);
    auto readSet = new SocketSet();
    Socket[] connectedClients;
    Socket selectedClient;
    char[1024] buffer;
    bool isRunning = true;
    while(isRunning) {
        readSet.reset();
        readSet.add(listener);
        foreach(client; connectedClients) readSet.add(client);
        if(Socket.select(readSet, null, null)) {
            foreach(client; connectedClients)
                if(readSet.isSet(client)) {
                    // Read from it and echo it back
                    auto got = client.receive(buffer);
                    writeln(buffer[0..got]);
                }
            if(readSet.isSet(listener)) {
                // The listener is ready to read, which means
                // a new client wants to connect. We accept it here.
                auto newSocket = listener.accept();
                newSocket.send("Hello!\n"); // say hello
                connectedClients ~= newSocket; // add to our list
                if(selectedClient is null) {
                    selectedClient = newSocket;
                }
            }
        }
    }
}
