package communication.netty;

import communication.CommunicationHandler;
import communication.CommunicationManager;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.File;

/**
 * A factory for creating a {@link NettyCommunicationManager}.
 */
public class NettyCommunicationManagerFactory {

    CommunicationHandler handler;
    int port;
    File cert;
    File key;

    /**
     * @param handler the handler managing incoming requests
     * @param port    the port to be used for communication
     * @param cert    a cert file used for secure communication
     * @param key     a private key file used for secure communication
     */
    public NettyCommunicationManagerFactory(CommunicationHandler handler, int port, File cert, File key) {
        this.handler = handler;
        this.port = port;
        this.cert = cert;
        this.key = key;
    }

    /**
     * @return a secure {@link CommunicationManager} if valid cert and key files were provided, otherwise an insecure {@link CommunicationManager}
     */
    public CommunicationManager create() {
        SslContext sslContext = null;
        try {
            sslContext = SslContextBuilder.forServer(cert, key).build();
        } catch (Exception e) {

        }
        NettyCommunicationManager manager;
        if(sslContext == null) {
            manager = new NettyCommunicationManager(handler, port);
        } else {
            manager = new NettyCommunicationManager(handler, port, sslContext);
        }
        return manager;
    }
}
