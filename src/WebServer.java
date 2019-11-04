import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    private String directoryPath;
    private int portNumber;
    private ServerSocket serverSocket;

    public WebServer(String directoryPath, int portNumber) {
        this.directoryPath = directoryPath;
        this.portNumber = portNumber;
        try {
            this.serverSocket = startServer();
            serverListening();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private ServerSocket startServer() throws IOException {
        ServerSocket ss = new ServerSocket(this.portNumber);
        System.out.println("Server started. Listening on port: " + this.portNumber);
        return ss;
    }

    private void serverListening() throws IOException {
        while (true) {
            Socket connection = this.serverSocket.accept();
            System.out.println("New connection from: " + connection.getInetAddress());
            ConnectionHandler connectionHandler = new ConnectionHandler(connection);
            connectionHandler.handleClientRequest();
        }
    }

}
