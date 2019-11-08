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
    private int portNumber;
    private int maxNumberOfThreads;
    private ServerSocket serverSocket;

    /**
     * Constructor. Parses the path to the directory the server serves documents from and the port number. Calls methods
     * to initialise the server socket and start listening for incoming clients.
     *
     * @param webDirectoryPath The root directory from which the server will serve documents.
     * @param portNumber The port on which the server will listen.
     * @param maxNumberOfThreads The maximum number of active threads.
     */
    public WebServer(String webDirectoryPath, int portNumber, int maxNumberOfThreads) {
        this.webDirectoryPath = webDirectoryPath;
        this.portNumber = portNumber;
        this.maxNumberOfThreads = maxNumberOfThreads;
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
        while (Thread.activeCount() < this.maxNumberOfThreads) {
            Socket connection = this.serverSocket.accept();
            System.out.println("\nNew connection from: " + connection.getInetAddress());
            ConnectionHandler connectionHandler = new ConnectionHandler(connection, this.webDirectoryPath);
            connectionHandler.start(); // Start threaded connection handler.
        }
    }

}
