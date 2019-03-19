import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClientWorker {

    private Socket clientSocket;


    public ClientWorker(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
        clientHandler = new ClientHandler(this);
    }


    public boolean isConnected() {
        return clientSocket != null && !clientSocket.isClosed();
    }


    public void run() {
        try {
            clientHandler.handle();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                disconnect();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

 
    public void disconnect() throws IOException {
        LOGGER.info(getString("server.cleaningUpResources"));

        for (ClientWorker clientWorker : CLIENT_WORKER_LIST) {
            if (clientWorker == this) {
                CLIENT_WORKER_LIST.remove(clientWorker);
                break;
            }
        }

        
        close(inputStream);
        close(outputStream);
        close(clientSocket);
    }


    private void close(Closeable closeable) throws IOException {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw e;
            }
        }
    }

   private String getString(String key) {

        
        return new String(getString(key).getBytes(
                StandardCharsets.ISO_8859_1),
                StandardCharsets.UTF_8);
    }
}