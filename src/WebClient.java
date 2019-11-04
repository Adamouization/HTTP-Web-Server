import java.io.*;
import java.net.Socket;

/**
 *
 */
public class WebClient {

    // Declare and initialise variables.
    private Socket socket;
    private String hostName;
    private int portNumber;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    /**
     *
     * @param hostName
     * @param portNumber
     */
    public WebClient (String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        runClient();
    }

    /**
     *
     */
    private void runClient() {
        try {
            this.socket = new Socket(this.hostName, this.portNumber);
            System.out.println("WebClient: connected to " + this.hostName + " on port " + this.portNumber + ".");
            this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            this.printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                String line = this.bufferedReader.readLine();
                this.printWriter.println(line);
                this.printWriter.flush();
            }
        }
        catch (Exception e) {
            System.out.println("WebClient: error while connection to " + this.hostName + " on port " + this.portNumber + ". " + e.getMessage());
            cleanup();
        }
    }

    /**
     *
     */
    private void cleanup() {
        System.out.println("WebClient: cleaning up and exiting." );
        try {
            if(this.printWriter != null) printWriter.close();
            if(this.bufferedReader != null) bufferedReader.close();
            if(socket != null) socket.close();
        } catch (IOException ioe){
            System.out.println("WebClient#cleanup: " + ioe.getMessage());
        }
    }

}
