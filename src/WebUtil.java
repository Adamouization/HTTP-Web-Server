import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * Class containing the general final variables and methods useful to support a web server.
 *
 * @author 150014151
 *
 */
public class WebUtil {

    // Declare and initialise final variables.
    public static final String CRLF = "'\r\n'"; // <cr><lf> (Carriage Return Line Feed).
    public static final String DEFAULT_FILE = "index.html"; // Default html page.
    public static final String FILE_NOT_FOUND = "errors/404.html"; // Page for 404 Errors.
    public static final String FILE_METHOD_NOT_IMPLEMENTED = "errors/501.html"; // Page for 501 Errors.

    /**
     * Converts a file's content to an array of bytes in order to send it over a BufferedOutputStream.
     *
     * @param file The file to convert to an array of bytes.
     * @param fileLength The length of the file.
     * @return An array of bytes containing the file's content.
     * @throws IOException When the file isn't found.
     */
    public static byte[] fileDataToBytes(File file, int fileLength) throws IOException {
        // Declare and initialise local variables.
        FileInputStream fileInputStream = null; // Initialise FileInputStream instance to read file.
        byte[] fileDataBytes = new byte[fileLength]; // Initialise array of bytes holding file contents.

        // Converts the content into bytes.
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileDataBytes);
        }

        // Close the FileInputStream.
        finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }

        return fileDataBytes;
    }

}
