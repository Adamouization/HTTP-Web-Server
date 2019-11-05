import exception.DisconnectionException;

import java.io.*;
import java.net.Socket;

/**
 *
 */
public class ConnectionHandler {

    // Declare variables.
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader bufferedReader;

    /**
     *
     * @param socket
     */
    public ConnectionHandler(Socket socket) {
        this.socket = socket;
        try {
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
        }
        catch (IOException ioe) {
            System.out.println("Server ConnectionHandler#ConnectionHandler: " + ioe.getMessage());
        }
        System.out.println("ConnectionHandler successfully instantiated.");
    }

    /**
     * Handles incoming requests from the client.
     */
    public void handleClientRequest() {
        System.out.println("Server ConnectionHandler: handling client connection");
        try {
            while (true) {
                String line = this.bufferedReader.readLine();
                if (line.startsWith("HEAD")) {
                    // todo head
                }
                else if (line.startsWith("GET")) {
                    // todo get
                }
                if (line == null || line.equals("null") ) {
                    throw new DisconnectionException("ConnectionHandler: client has closed the connection ...");
                }
                System.out.println("ConnectionHandler, message received: " + line);
            }
        }
        catch (Exception e) {
            System.out.println("ConnectionHandler#handleClientRequest: " + e.getMessage());
        }
    }

    /**
     * Elegantly cleans the up the environment by closing sockets and I/O objects.
     */
    private void cleanupConnection() {
        System.out.println("Server ConnectionHandler: cleaning up and exiting." );
        try {
            if (this.bufferedReader != null) {
                this.bufferedReader.close();
            }
            if (this.inputStream != null) {
                this.inputStream.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException ioe){
            System.out.println("Server ConnectionHandler#cleanup: " + ioe.getMessage());
        }
    }

}
