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

    /**
     * Default html page.
     */
    public static final String DEFAULT_FILE = "index.html";

    /**
     * Page for 404 Errors.
     */
    public static final String FILE_NOT_FOUND = "errors/404.html";

    /**
     * Page for 501 Errors.
     */
    public static final String FILE_METHOD_NOT_IMPLEMENTED = "errors/501.html";

    /**
     * Carriage Return Line Feed.
     */
    public static final String CR_LF = "\r\n";

    /**
     * Code for OK responses.
     */
    public static final int CODE_OK = 200;

    /**
     * Code for No Content responses.
     */
    public static final int CODE_NO_CONTENT = 204;

    /**
     * Code for Not Found responses.
     */
    public static final int CODE_NOT_FOUND = 404;

    /**
     * Code for Not Implemented responses.
     */
    public static final int CODE_NOT_IMPLEMENTED = 501;

    /**
     * Converts a file's content to an array of bytes in order to send it over a BufferedOutputStream. Works with any
     * file type, including text files like HTML and binary image files like JPG, JPEG, PNG and GIF.
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
