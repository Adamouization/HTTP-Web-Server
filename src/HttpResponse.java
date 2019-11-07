import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class HttpResponse {

    private PrintWriter printWriter;
    private BufferedOutputStream bufferedOutputStream;

    public HttpResponse(PrintWriter pw, BufferedOutputStream bos, int responseCode, String httpMethod, String fileRequested, String webRoot) {
        this.printWriter = pw;
        this.bufferedOutputStream = bos;

        try {
            // Prepare the requested file to send back.
            File file = new File(webRoot + "/" + fileRequested);
            int fileLength = (int) file.length();
            // Send back data.
            switch (responseCode) {
                case 200:
                    // Send back HTTP response header to the client.
                    sendHttpResponseHeader("200 OK", fileLength);
                    // Send back the HTTP response content to the client.
                    if (httpMethod.equals("GET")) {
                        sendHttpResponseContent(file);
                    }
                    break;
                case 404:
                    sendHttpResponseHeader("404 Not Found", fileLength);
                    sendHttpResponseContent(file);
                    break;
                case 501:
                    sendHttpResponseHeader("501 Not Implemented", fileLength);
                    sendHttpResponseContent(file);
                    break;
            }
        }
        catch (IOException ioe) {
            System.err.println("ConnectionHandler: file 404.html not found\nError: " + ioe.getMessage());
        }
    }

    /**
     * Prepares the header of the HTTP response and sends it back to the client.
     *
     * @param statusLine The status line of the header.
     * @param fileLength The length of the response's content being sent back to the client.
     */
    private void sendHttpResponseHeader(String statusLine, int fileLength) {
        this.printWriter.println("HTTP/1.1 " + statusLine);
        this.printWriter.println("Server: Simple Java HTTP Server");
        this.printWriter.println("Content-Type: text/html");
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

}
