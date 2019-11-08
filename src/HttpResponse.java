import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * Class creating the various HTTP response to send back to the client, preparing the header and content for "200 OK",
 * "404 Error Not Found" and "501 Error Not Implemented" HTTP responses.
 *
 * @author 150014151
 *
 */
public class HttpResponse {

    // Declare variables.
    private PrintWriter printWriter;
    private BufferedOutputStream bufferedOutputStream;

    /**
     * Constructor. Sends the following HTTP responses back to the client based on the request made:
     *              - A 200 OK header for a HEAD request,
     *              - A 200 OK header and the desired file's content for a GET request,
     *              - A 404 Error header with a custom html page (errors/404.html) when the requested file is not
     *              found,
     *              - A 501 Not Implemented header with a custom html page (errors/501.html) when the requested method
     *              isn't implemented.
     * @param pw The PrintWriter object used to send the HTTP response header to the client.
     * @param bos The BufferedOutputStream used to send the HTTP response content to the client.
     * @param responseCode The HTTP response code used to build an appropriate response header.
     * @param httpMethod The HTTP request made by the client.
     * @param fileRequested The file requested in the HTTP request made by the client.
     * @param contentType The type of the content being sent back to the client.
     * @param webRoot The root directory from which the server will serve documents.
     */
    public HttpResponse(PrintWriter pw, BufferedOutputStream bos, int responseCode, String httpMethod,
                        String fileRequested, String contentType, String webRoot) {
        this.printWriter = pw;
        this.bufferedOutputStream = bos;
        try {
            // Prepare the requested file to send back.
            File file = new File(webRoot + "/" + fileRequested);
            int fileLength = (int) file.length();

            // Try to carry out the DELETE request.
            if (httpMethod.equals("DELETE")) {
                responseCode = deleteAndGetResponseCode(file, fileRequested);
            }

            // Send HTTP response (headers then content) back to client.
            switch (responseCode) {
                case WebUtil.CODE_OK:
                    sendHttpResponseHeader(
                            "200 OK",
                            contentType,
                            fileLength
                    );
                    if (httpMethod.equals("GET")) { // Only send if GET request, not HEAD.
                        sendHttpResponseContent(file);
                    }
                    break;
                case WebUtil.CODE_NO_CONTENT:
                    sendHttpResponseHeader(
                            "204 No Content",
                            "text/html", // Always html.
                            fileLength
                    );
                    break;
                case WebUtil.CODE_NOT_FOUND:
                    sendHttpResponseHeader(
                            "404 Not Found",
                            "text/html", // Always html.
                            fileLength
                    );
                    sendHttpResponseContent(file);
                    break;
                default:
                    sendHttpResponseHeader(
                            "501 Not Implemented",
                            "text/html", // Always html.
                            fileLength
                    );
                    sendHttpResponseContent(file);
                    break;
            }

            // Log the information of the client's HTTP request.
            WebLogger webLogger = new WebLogger("../http_requests_history.log");
            webLogger.logRequest(httpMethod, responseCode, fileRequested);
            webLogger.saveLogFile();
            System.out.println("HttpResponse: responded with response code '" + responseCode + "'");
        }
        catch (IOException ioe) {
            System.err.println("ConnectionHandler: file '" + fileRequested + "' not found\nError: " + ioe.getMessage());
        }
    }

    /**
     * Prepares the header of the HTTP response and sends it back to the client.
     *
     * @param statusLine The status line of the header.
     * @param fileLength The length of the response's content being sent back to the client.
     */
    private void sendHttpResponseHeader(String statusLine, String contentType, int fileLength) {
        this.printWriter.println("HTTP/1.1 " + statusLine);
        this.printWriter.println("Server: Simple Java HTTP Server");
        this.printWriter.println("Content-Type: " + contentType);
        this.printWriter.println("Content-Length: " + fileLength);
        this.printWriter.println();
        this.printWriter.flush();
    }

    /**
     * Prepares the content of the HTTP response and sends it back to the client.
     *
     * @param file The file whose content is being sent back to the client in the body of the HTTP response.
     * @throws IOException When the file whose content are to sent cannot be found.
     */
    private void sendHttpResponseContent(File file) throws IOException {
        int fileLength = (int) file.length();
        this.bufferedOutputStream.write(WebUtil.fileDataToBytes(file, fileLength), 0, fileLength);
        this.bufferedOutputStream.flush();
    }

    /**
     * Tries to delete the requested file. Returns a 204 No Content response if the file is successfully deleted (there
     * is no content returned to the client) or a 404 Not Found response if the file cannot be deleted (because it isn't
     * there).
     *
     * @param file The file to delete.
     * @param fileRequested the name of the file to delete.
     * @return The appropriate response code based on the outcome of the delete operation.
     */
    private int deleteAndGetResponseCode(File file, String fileRequested) {
        if (file.delete()) {
            System.out.println("Deleted request file '" + fileRequested + "'");
            return WebUtil.CODE_NO_CONTENT;
        }
        else {
            System.out.println("Failed to delete the requested file '" + fileRequested + "'");
            return WebUtil.CODE_NOT_FOUND;
        }
    }

}
