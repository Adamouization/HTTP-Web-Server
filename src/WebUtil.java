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

    // Declare and initialise variables.
    public static final String fileNotFound = "404.html";
    public static final String fileMethodNotImplemented = "501.html";

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
        byte[] fileData = new byte[fileLength]; // Initialise array of bytes holding file contents.

        // Converts the content into bytes.
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileData);
        }

        // Close the FileInputStream.
        finally {
            if (fileInputStream != null)
                fileInputStream.close();
        }

        return fileData;
    }

}
