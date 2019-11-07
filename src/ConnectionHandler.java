import exception.DisconnectionException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 *
 * Class containing the methods to process incoming HTTP requests in parallel by extending the Thread class.
 *
 * @author 150014151
 *
 */
public class ConnectionHandler extends Thread {

    // Declare variables.
    private String webRoot;
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private BufferedOutputStream bufferedOutputStream;

    /**
     * Constructor. Initialises the input and output streams used to receive data from the clients and to send data
     * back.
     *
     * @param socket The client connection.
     * @param webRoot The root directory from which the server will serve documents.
     */
    public ConnectionHandler(Socket socket, String webRoot) {
        this.webRoot = webRoot;
        this.socket = socket;
        try {
            InputStream inputStream = this.socket.getInputStream();
            OutputStream outputStream = this.socket.getOutputStream();
            this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.printWriter = new PrintWriter(outputStream);
            this.bufferedOutputStream = new BufferedOutputStream(outputStream);
        }
        catch (IOException ioe) {
            System.out.println("Server ConnectionHandler#ConnectionHandler: " + ioe.getMessage());
        }
        System.out.println("ConnectionHandler successfully instantiated.");
    }

    /**
     * The main HTTP connection handler. Parses the incoming lines from the client before deciding what to send back to
     * the client. Supports the following HTTP requests: HEAD and GET requests.
     * Sends the following back to the client based on the request:
     *      - The desired header (200) for a HEAD request,
     *      - The desired header (200) and content (the html page) for a GET request,
     *      - An error header (404) with a custom html page (errors/404.html) when the requested file is not found,
     *      - An error header (501) with a custom html page (errors/501.html) when the requested method isn't implemented.
     * This method is invoked when the thread's start method is called.
     */
    public void run() {
        // Declare local variable.
        String httpMethod, fileRequested;
        StringTokenizer tokenizedLine;
        File file;

        System.out.println("ConnectionHandler: New thread started to handle connection");
        try {
            // Read incoming line from client.
            String line = this.bufferedReader.readLine();

            // Close the connection when readLine fails (broken connection to client).
            if (line == null || line.isEmpty()) {
                throw new DisconnectionException("Client has closed the connection ...");
            }

            // Parse the line into multiple tokens (break the main String into multiple strings).
            System.out.println("Incoming message from client: " + line);
            tokenizedLine = new StringTokenizer(line);

            // Extract requested HTTP method and requested file from client.
            httpMethod = tokenizedLine.nextToken().toUpperCase(); // Ensure method is all in upper cases.
            fileRequested = tokenizedLine.nextToken().toLowerCase(); // Ensure file requested is all in lower cases.

            // Point default route towards index.html.
            if (fileRequested.endsWith("/")) {
                fileRequested += WebUtil.DEFAULT_FILE;
            }
            System.out.println("ConnectionHandler: file " + fileRequested + " requested");

            // Return a 501 error if requested HTTP method is not supported by the server (GET and HEAD only).
            if (!httpMethod.equals("GET") && !httpMethod.equals("HEAD") && !fileRequested.contains(".ico")) {
                System.out.println("ConnectionHandler: Request method '" + httpMethod + "' not implemented.");
                new HttpResponse(this.printWriter, this.bufferedOutputStream, 501, httpMethod, WebUtil.FILE_METHOD_NOT_IMPLEMENTED, this.webRoot);
            }
            // Requested HTTP method is supported by the web server.
            else {
                file = new File(this.webRoot + fileRequested);
                // Return a 404 error because requested file does not exist.
                if (!file.exists()) {
                    new HttpResponse(this.printWriter, this.bufferedOutputStream,404, httpMethod, WebUtil.FILE_NOT_FOUND, this.webRoot);
                }
                // Return the requested file.
                else {
                    new HttpResponse(this.printWriter, this.bufferedOutputStream,200, httpMethod, fileRequested, this.webRoot);
                }
            }
        }
        catch (NoSuchElementException nsee) {
            System.out.println("Cannot tokenize empty incoming message from client: " + nsee.getMessage());
        }
        catch (Exception e) {
            System.out.println("ConnectionHandler.run: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            cleanupConnection();
        }
    }

    /**
     * Elegantly cleans the up the environment by closing sockets and I/O objects.
     */
    private void cleanupConnection() {
        System.out.println("ConnectionHandler: cleaning up and exiting");
        try {
            if (this.bufferedReader != null) {
                this.bufferedReader.close();
            }
            if (this.printWriter != null) {
                this.printWriter.close();
            }
            if (this.bufferedOutputStream != null) {
                this.bufferedOutputStream.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
        }
        catch (IOException ioe) {
            System.out.println("ConnectionHandler: error while closing streams and socket : " + ioe.getMessage());
        }
    }

}
