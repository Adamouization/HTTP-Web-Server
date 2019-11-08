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
    private String clientIpAddress;
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
     * @param clientIpAddress The String representing the IP address of the connected client.
     */
    public ConnectionHandler(Socket socket, String webRoot, String clientIpAddress) {
        this.webRoot = webRoot;
        this.clientIpAddress = clientIpAddress;
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
    }

    /**
     * The main HTTP connection handler. Parses the incoming lines from the client before deciding what HTTP response to
     * send back to the client. Supports the following HTTP requests: HEAD and GET requests. Creates a new HttpResponse
     * object according to the request made by the client:
     *      - The desired file and its content if the file exists,
     *      - A 404 Error if the requested file does not exist,
     *      - A 501 Error if the requested method is not implemented by the server.
     * This method is invoked when the thread's start method is called.
     */
    public void run() {
        // Declare local variable.
        String httpMethod, fileRequested, contentType;
        StringTokenizer tokenizedLine;
        File file;

        System.out.println("ConnectionHandler: new thread started to handle incoming connection");
        try {
            // Read incoming line from client.
            String line = this.bufferedReader.readLine();

            // Check if the connection to the client is still open.
            isClientConnectionClosed(line);

            // Parse the line into multiple tokens and extract requested HTTP method and requested file from client.
            System.out.println("Incoming message from client: " + line);
            tokenizedLine = new StringTokenizer(line);
            httpMethod = tokenizedLine.nextToken().toUpperCase(); // Ensure method is all in upper cases.
            System.out.println("ConnectionHandler: Requested HTTP method '" + httpMethod + "'");
            fileRequested = parseFileRequested(tokenizedLine.nextToken());
            contentType = getContentType(fileRequested);

            // Check if requested HTTP method is not supported by the server (GET and HEAD requests supported only).
            if (!httpMethod.equals("GET") && !httpMethod.equals("HEAD") && !httpMethod.equals("DELETE")
                    && !fileRequested.contains(".ico")) {
                // Return a 501 Error Not Implemented response (not GET or HEAD).
                System.out.println("ConnectionHandler: 501 Request method '" + httpMethod + "' not implemented.");
                new HttpResponse(
                        this.printWriter,
                        this.bufferedOutputStream,
                        WebUtil.CODE_NOT_IMPLEMENTED,
                        httpMethod,
                        WebUtil.FILE_METHOD_NOT_IMPLEMENTED,
                        contentType,
                        this.webRoot,
                        this.clientIpAddress
                );
            }
            // Requested HTTP method is supported by the web server.
            else {
                file = new File(this.webRoot + fileRequested);
                // Return a 404 Error Not Found response (requested file does not exist).
                if (!file.exists()) {
                    System.out.println("ConnectionHandler: 404 requested file '" + fileRequested + "' not found.");
                    new HttpResponse(
                            this.printWriter,
                            this.bufferedOutputStream,
                            WebUtil.CODE_NOT_FOUND,
                            httpMethod,
                            WebUtil.FILE_NOT_FOUND,
                            contentType,
                            this.webRoot,
                            this.clientIpAddress
                    );
                }
                // Return a 200 OK response (with the requested file).
                else {
                    new HttpResponse(
                            this.printWriter,
                            this.bufferedOutputStream,
                            WebUtil.CODE_OK,
                            httpMethod,
                            fileRequested,
                            contentType,
                            this.webRoot,
                            this.clientIpAddress
                    );
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
     * Close the connection when readLine fails (broken connection to client).
     *
     * @param line The incoming request from the client.
     * @throws DisconnectionException When line is null or empty.
     */
    private void isClientConnectionClosed(String line) throws DisconnectionException {
        if (line == null || line.isEmpty()) {
            throw new DisconnectionException("Client has closed the connection ...");
        }
    }

    /**
     * Parses the requested file String by ensuring it is composed of lower case characters only and that if there is no
     * requested file (only '/' is requested), then the default file "index.html" is returned as a response instead.
     *
     * @param fileRequested The file requested by the client.
     * @return The parsed file requested by the client.
     */
    private String parseFileRequested(String fileRequested) {
        // Ensure file requested is all in lower cases.
        fileRequested = fileRequested.toLowerCase();

        // Point default route towards index.html.
        if (fileRequested.endsWith("/")) {
            fileRequested += WebUtil.DEFAULT_FILE;
        }

        System.out.println("ConnectionHandler: file " + fileRequested + " requested");
        return fileRequested;
    }

    /**
     * Determines the content-type of the requested filed used in the HTTP response header. Supports HTMl, JPG, JPEG,
     * PNG and GIF files. Defaults to plain text files.
     *
     * @param fileRequested The file requested by the client.
     * @return The content-type of the requested file.
     */
    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".html")) {
            return "text/html";
        }
        else if (fileRequested.endsWith(".jpg")) {
            return "image/jpg";
        }
        else if (fileRequested.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        else if (fileRequested.endsWith(".png")) {
            return "image/png";
        }
        else if (fileRequested.endsWith(".gif")) {
            return "image/gif";
        }
        else {
            return "text/plain";
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
