import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {

  
    public void start() throws IOException {

       
        if (serverSocket == null || !serverSocket.isBound()) {
            try {
                bindOnKnownPort(listenPort);
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                throw e;
            }
        }

        
        new Thread(() -> {
            shouldRun = true;

            while (shouldRun) {
                logger.info(configurationManager.getString("server.numberClients",
                        ClientWorker.getClientWorkerList().size()));

                logger.info(configurationManager.getString("server.listeningClientConnection",
                        serverSocket.getLocalSocketAddress()));

                try {
                    Socket clientSocket = serverSocket.accept();
                    logger.info(configurationManager.getString("server.clientArrived"));
                    logger.info(configurationManager.getString("server.delegatingWork"));

                    ClientWorker clientWorker = new ClientWorker(clientSocket);
                    new Thread(clientWorker).start();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    shouldRun = false;
                }
            }
        }).start();
    }

 
    private void bindOnKnownPort(int port) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));
    }

  
    private void close() throws IOException {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                throw e;
            }
        }
    }

  
    public void stop() throws IOException {
        shouldRun = false;
        logger.info(configurationManager.getString("server.cleaningUpResources"));
        for (ClientWorker clientWorker : ClientWorker.getClientWorkerList()) {
            clientWorker.disconnect();
        }

        close();
    }


    private static class SingletonHolder {
        private final static Server instance = new Server();
    }
}