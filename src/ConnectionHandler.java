import exception.DisconnectionException;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 *
 */
public class ConnectionHandler extends Thread {

    // Declare variables.
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private BufferedOutputStream bufferedOutputStream;

    /**
     *
     * @param socket
     */
    public ConnectionHandler(Socket socket) {
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

    public void run() {
        // Declare local variable.
        String httpMethod, fileRequested;
        StringTokenizer tokenizedLine;

        System.out.println("Server ConnectionHandler: new thread started to handle connection");
        try {
            while (true) {
                // Read incoming line from client.
                String line = this.bufferedReader.readLine();

                // Parse the line into multiple tokens (individual strings).
                tokenizedLine = new StringTokenizer(line);
                httpMethod = tokenizedLine.nextToken().toUpperCase(); // Ensure method is all in upper cases.
                fileRequested = tokenizedLine.nextToken().toLowerCase(); // Ensure file requested is all in lower cases.

                // Close the connection when readLine fails (broken connection to client).
                if (line.equals("null")) {
                    throw new DisconnectionException("ConnectionHandler: client has closed the connection ...");
                }

                // Check if requested HTTP method is supported by the web server.
                if (!httpMethod.equals("GET") && !httpMethod.equals("HEAD")) {

                    File file = new File("www/501.html");
                    int fileLength = (int) file.length();

                    // Send back HTTP headers to the client.
                    this.printWriter.println("HTTP/1.1 501 Not Implemented");
                    this.printWriter.println("Server: Simple Java HTTP Server");
                    this.printWriter.println("Content-Type: text/html");
                    this.printWriter.println("Content-Length:" + (int) file.length());
                    this.printWriter.println();
                    this.printWriter.flush();
                    // Send back the HTTP body to the client.
                    this.bufferedOutputStream.write(fileDataToBytes(file, fileLength), 0, fileLength);
                    this.bufferedOutputStream.flush();
                }
                // Request HTTP method is supported by the web server.
                else {
                    // Prepare HEAD request.
                    File file = new File("www" + fileRequested);
                    int fileLength = (int) file.length();

                    this.printWriter.println("HTTP/1.1 200 OK");
                    this.printWriter.println("Server: Simple Java HTTP Server");
                    this.printWriter.println("Content-Type: text/html");
                    this.printWriter.println("Content-Length:" + (int) file.length());
                    this.printWriter.println();
                    this.printWriter.flush();

                    // Prepare body of GET request.
                    if (line.startsWith("GET")) {
                        this.bufferedOutputStream.write(fileDataToBytes(file, fileLength), 0, fileLength);
                        this.bufferedOutputStream.flush();
                    }
                }

                System.out.println("Incoming message from client: " + line);
            }
        }
        catch (Exception e) {
            System.out.println("ConnectionHandler.run: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            cleanupConnection();
        }
    }

    private byte[] fileDataToBytes(File file, int fileLength) throws IOException {
        // Declare and initialise local variables.
        FileInputStream fileInputStream = null; // Initialise FileInputStream instance to read file.
        byte[] fileData = new byte[fileLength]; // Initialise array of bytes holding file contents.
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileData);
        } finally {
            if (fileInputStream != null)
                fileInputStream.close();
        }
        return fileData;
    }

    /**
     * Elegantly cleans the up the environment by closing sockets and I/O objects.
     */
    private void cleanupConnection() {
        System.out.println("ConnectionHandler: cleaning up and exiting." );
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
        } catch (IOException ioe){
            System.out.println("ConnectionHandler: error while closing streams and socket : " + ioe.getMessage());
        }
    }

}
