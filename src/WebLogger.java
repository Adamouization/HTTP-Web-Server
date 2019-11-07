import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * Class that logs information about the HTTP request being handled to a plain text file.
 *
 * @author 150014151
 *
 */
class WebLogger {

    // Declare variables.
    private FileWriter fileWriter;
    private PrintWriter printWriter;
    private DateTimeFormatter dateTimeFormatter;

    /**
     * Constructor. Creates the file at the desired path creates a DateTimeFormatter to format the date.
     *
     * @param path The path of the log file.
     * @throws IOException When the path to the log file is wrong.
     */
    WebLogger(String path) throws IOException {
        this.fileWriter = new FileWriter(path, true); // Don't overwrite existing logs, add to the file.
        this.printWriter = new PrintWriter(this.fileWriter);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    }

    /**
     * Writes a new entry to the log, starting with the date, followed by information about the request.
     *
     * @param httpMethod The HTTP request made by the client.
     * @param responseCode The HTTP response code used to build an appropriate response header.
     * @param fileRequested The file requested in the HTTP request made by the client.
     */
    public void logRequest(String httpMethod, int responseCode, String fileRequested) {
        LocalDateTime now = LocalDateTime.now();
        this.printWriter.println(this.dateTimeFormatter.format(now));
        this.printWriter.println("HTTP method: '" + httpMethod + "' requesting file '" + fileRequested +
                "'. Server responded with code '" + responseCode + "'");
        this.printWriter.println();
    }

    /**
     * Saves the CSV file and closes the I/O streams.
     *
     * @throws IOException When the path to the log file is wrong.
     */
    void saveLogFile() throws IOException {
        this.printWriter.flush();
        this.printWriter.close();
        this.fileWriter.close();
    }

}