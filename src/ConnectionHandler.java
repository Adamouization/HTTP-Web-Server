import java.io.*;
import java.net.Socket;

public class ConnectionHandler {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader bufferedReader;

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

    public void handleClientRequest() {
        System.out.println("Server ConnectionHandler: handling client connection");
        try {
            while (true) {
                String line = this.bufferedReader.readLine();
                if (line == null || line.equals("null") ) {
                    throw new DisconnectionException("ConnectionHandler: client has closed the connection ...");
                }
                System.out.println("ConnectionHandler#handleClientRequest: " + line);
            }
        }
        catch (Exception e) {
            System.out.println("ConnectionHandler#handleClientRequest: " + e.getMessage());
        }
    }

    public void cleanupConnection() {
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
