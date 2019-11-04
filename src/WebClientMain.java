public class WebClientMain {

    public static void main(String[] args) {
        if (args.length == 1) {
            new WebClient(args[0], 12345);
        }
        else {
            clientErrorMessage();
        }
    }

    /**
     * Prints an error message to the command line and terminates the program with an error code.
     */
    public static void clientErrorMessage() {
        System.out.println("usage: java WebClientMain <hostname> <port>");
        System.exit(1); // System exit with an error code.
    }

}
