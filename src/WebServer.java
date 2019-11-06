import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * Class containing the methods to handle new incoming client connections.
 *
 * @author 150014151
 *
 */
public class WebServer {

    // Declare variables.
    private String webDirectoryPath;
    private String fileNotFound;
    private String fileMethodNotImplemented;
    private int portNumber;
    private ServerSocket serverSocket;

    /**
     * Constructor. Parses the path to the directory the server serves documents from and the port number. Calls methods
     * to initialise the server socket and start listening for incoming clients.
     *
     * @param webDirectoryPath The root directory from which the server will serve documents.
     * @param portNumber The port on which the server will listen.
     */
    public WebServer(String webDirectoryPath, int portNumber) {
        this.webDirectoryPath = webDirectoryPath;
        this.fileNotFound = "../www/404.html";
        this.fileMethodNotImplemented = "../www/501.html";
        this.portNumber = portNumber;
        try {
            this.serverSocket = startServer();
            serverListening();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Initialises the server socket.
     *
     * @return The ServerSocket
     * @throws IOException If the server isn't properly initialised.
     */
    private ServerSocket startServer() throws IOException {
        ServerSocket ss = new ServerSocket(this.portNumber);
        System.out.println("Server started. Listening on port: " + this.portNumber);
        return ss;
    }

    /**
     * Starts listening for incoming clients connecting. Initialises a new ConnectionHandler instance for each new
     * client that connects.
     *
     * @throws IOException If the server isn't properly initialised.
     */
    private void serverListening() throws IOException {
        while (true) {
            Socket connection = this.serverSocket.accept();
            System.out.println("\nNew connection from: " + connection.getInetAddress());
            ConnectionHandler connectionHandler = new ConnectionHandler(connection);
            connectionHandler.start(); // Start threaded connection handler.
        }
    }

}
