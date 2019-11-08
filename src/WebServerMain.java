/**
 *
 * Contains the program entry point. Parses the command line arguments and initialises a new Web Server.
 *
 * @author 150014151
 *
 */
public class WebServerMain {

    private static final int MAX_ARGUMENTS = 3; // Max number of arguments.
    private static final int DEFAULT_MAX_NUMBER_OF_THREADS = 100; // Default value for max number of active threads.
    private static final int MAX_PORT_NUMBER = 65535; // Max value for a 16-bit integer (2^16 - 1 = 65535).

    /**
     * Program entry point.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Declare and initialise variables.
        String documentRoot = "";
        int portNumber = 0;
        int maxNumberOfThreads = DEFAULT_MAX_NUMBER_OF_THREADS; // Default value.

        // Parse command line arguments (2 expected, 3 if specifying max number of threads).
        if (!(args.length < 2 || args.length > MAX_ARGUMENTS)) {
            // Parse document root.
            documentRoot = args[0];
            // Parse port number.
            try {
                portNumber = Integer.parseInt(args[1]);
                // Invalid port number (must be a positive integer smaller than a )
                if (portNumber < 0 || portNumber > MAX_PORT_NUMBER) {
                    serverErrorMessage();
                }
            }
            catch (NumberFormatException e) {
                serverErrorMessage();
            }
            // Parse maximum number of threads (optional argument).
            if (args.length == MAX_ARGUMENTS) {
                try {
                    maxNumberOfThreads = Integer.parseInt(args[2]);
                    // Invalid port number (must be a positive integer smaller than a )
                    if (maxNumberOfThreads < 0) {
                        serverErrorMessage();
                    }
                }
                catch (NumberFormatException e) {
                    serverErrorMessage();
                }
            }
        }
        // Incorrect number of arguments.
        else {
            serverErrorMessage();
        }

        // Create new instance of the HTTP web server.
        new WebServer(documentRoot, portNumber, maxNumberOfThreads);
    }

    /**
     * Prints an error message to the command line and terminates the program with an error code.
     */
    public static void serverErrorMessage() {
        System.out.println("Usage: java WebServerMain <document_root> <port>");
        System.exit(1); // System exit with an error code.
    }

}
