import java.io.IOException;

public class WebServerMain {

    public static void main(String[] args) {

        // Declare and initialise variables.
        String documentRoot = "";
        int portNumber = 0;

        // Parse command line arguments (2 expected).
        if (args.length == 2){
            documentRoot = args[0];
            try {
                portNumber = Integer.parseInt(args[1]);
                if (portNumber < 0) {
                    serverErrorMessage();
                }
            }
            catch (NumberFormatException nfe) {
                serverErrorMessage();
            }
        }
        else {
            serverErrorMessage();
        }

        // Create new instance of the web server.
        new WebServer(documentRoot, portNumber);
    }

    /**
     * Prints an error message to the command line and terminates the program with an error code.
     */
    public static void serverErrorMessage() {
        System.out.println("usage: java WebServerMain <document_root> <port>");
        System.exit(1); // System exit with an error code.
    }

}
