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
     * Returns the following based on the request:
     *      - The desired header (200) for a HEAD request,
     *      - The desired header (200) and content (the html page) for a GET request,
     *      - An error header (404) with a custom html page (404.html) when the requested file is not found,
     *      - An error header (501) with a custom html page (501.html) when the requested method isn't implemented.
     * This method invoked when the thread's start method is called.
     */
    public void run() {
        // Declare local variable.
        String httpMethod, fileRequested;
        StringTokenizer tokenizedLine;

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
                fileRequested += WebUtil.defaultFile;
            }
            System.out.println("ConnectionHandler: file " + fileRequested + " requested");

            // Check if requested HTTP method is supported by the web server (can only support GET and HEAD).
            if (!httpMethod.equals("GET") && !httpMethod.equals("HEAD") && !fileRequested.contains(".ico")) {

                System.out.println("ConnectionHandler: Request method '" + httpMethod + "' not implemented.");
                // Prepare the custom html file for 501 errors to be sent back.
                File file = new File(this.webRoot + "/" + WebUtil.fileMethodNotImplemented);
                int fileLength = (int) file.length();

                // Send back HTTP header fields to the client.
                this.printWriter.println("HTTP/1.1 501 Not Implemented");
                this.printWriter.println("Server: Simple Java HTTP Server");
                this.printWriter.println("Content-Type: text/html");
                this.printWriter.println("Content-Length: " + fileLength);
                this.printWriter.println();
                this.printWriter.flush();
                // Send back the HTTP body to the client.
                this.bufferedOutputStream.write(WebUtil.fileDataToBytes(file, fileLength), 0, fileLength);
                this.bufferedOutputStream.flush();
            }

            // Requested HTTP method is supported by the web server.
            else {
                // Prepare HEAD request.
                File file = new File(this.webRoot + fileRequested);

                if (!file.exists()) {
                    try {
                        file = new File(this.webRoot + "/" + WebUtil.fileNotFound);
                        int fileLength = (int) file.length();

                        // Send back HTTP header fields to the client.
                        this.printWriter.println("HTTP/1.1 404 Not Found");
                        this.printWriter.println("Server: Simple Java HTTP Server");
                        this.printWriter.println("Content-Type: text/html");
                        this.printWriter.println("Content-Length: " + fileLength);
                        this.printWriter.println();
                        this.printWriter.flush();

                        // Prepare body of 404 Error.
                        this.bufferedOutputStream.write(WebUtil.fileDataToBytes(file, fileLength), 0, fileLength);
                        this.bufferedOutputStream.flush();
                    } catch (IOException ioe) {
                        System.err.println("ConnectionHandler: file 404.html not found\nError: " + ioe.getMessage());
                    }
                }
                else {
                    int fileLength = (int) file.length();

                    // Send back HTTP header fields to the client.
                    this.printWriter.println("HTTP/1.1 200 OK");
                    this.printWriter.println("Server: Simple Java HTTP Server");
                    this.printWriter.println("Content-Type: text/html");
                    this.printWriter.println("Content-Length: " + fileLength);
                    this.printWriter.println();
                    this.printWriter.flush();

                    // Prepare body of GET request.
                    if (line.startsWith("GET")) {
                        this.bufferedOutputStream.write(WebUtil.fileDataToBytes(file, fileLength), 0, fileLength);
                        this.bufferedOutputStream.flush();
                    }
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
