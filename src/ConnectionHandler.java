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
    private PrintWriter printWriter;

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
            this.printWriter = new PrintWriter(this.outputStream);
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
                // Close the connection when readLine fails (broken connection to client).
                if (line == null || line.equals("null")) {
                    throw new DisconnectionException("ConnectionHandler: client has closed the connection ...");
                }
                // Handle a HEAD request.
                if (line.startsWith("HEAD")) {
                    this.printWriter.println("HTTP/1.1 200 OK");
                    this.printWriter.flush();
                }
                // Handle a GET request.
                else if (line.startsWith("GET")) {
                    this.printWriter.println("HTTP/1.1 200 OK");
                    this.printWriter.flush();
                }
                // Request type not implemented
                else {
                    this.printWriter.println("HTTP/1.1 503 Not Implemented");
                    this.printWriter.flush();
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
