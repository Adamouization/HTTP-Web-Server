import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class WebServer {

    // Declare and initialise variables.
    private String webDirectoryPath;
    private String fileNotFound;
    private String methodNotImplemented;
    private int portNumber;
    private ServerSocket serverSocket;

    /**
     *
     * @param webDirectoryPath
     * @param portNumber
     */
    public WebServer(String webDirectoryPath, int portNumber) {
        this.webDirectoryPath = webDirectoryPath;
        this.fileNotFound = "../www/404.html";
        this.methodNotImplemented = "../www/501.html";
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
     *
     * @return
     * @throws IOException
     */
    private ServerSocket startServer() throws IOException {
        ServerSocket ss = new ServerSocket(this.portNumber);
        System.out.println("Server started. Listening on port: " + this.portNumber);
        return ss;
    }

    /**
     *
     * @throws IOException
     */
    private void serverListening() throws IOException {
        while (true) {
            Socket connection = this.serverSocket.accept();
            System.out.println("New connection from: " + connection.getInetAddress());
            ConnectionHandler connectionHandler = new ConnectionHandler(connection);
            connectionHandler.start(); // Start threaded connection handler.
        }
    }

}
