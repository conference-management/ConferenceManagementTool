package communication;

import communication.netty.NettyCommunicationManagerFactory;
import main.Conference;

import java.io.File;

/**
 * A factory for creating an {@link CommunicationManager} for a {@link Conference} handling incoming requests from the frontend.
 */
public class CommunicationManagerFactory {

    private Conference conference;
    private int port;
    private int timeoutAfter;
    private int maxUserConnections;
    private boolean debugging;
    private boolean ignoreSecurityAlerts;

    /**
     * Initialized the {@link CommunicationManagerFactory} using a not guaranteed to
     *
     * @param conference the conference to handle communication for
     */
    public CommunicationManagerFactory(Conference conference) {
        this.conference = conference;
        this.port = 17699;
        this.timeoutAfter = 10;
        this.maxUserConnections = 20;
        this.debugging = false;
    }

    /**
     * @param port the port to listen on for incoming requests
     *
     * @return this
     */
    public CommunicationManagerFactory setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * @param timeoutAfter the number of seconds a request is allowed to wait for a response before the connection gets forcefully closed
     *
     * @return this
     */
    public CommunicationManagerFactory setTimeoutAfter(int timeoutAfter) {
        this.timeoutAfter = timeoutAfter;
        return this;
    }

    /**
     * @param maxUserConnections the number of connections per user account which are allowed at the same time
     *
     * @return this
     */
    public CommunicationManagerFactory setMaxUserConnections(int maxUserConnections) {
        this.maxUserConnections = maxUserConnections;
        return this;
    }

    /**
     * Enables debugging functionaries such as fancy printing for JSON and more detailed logs
     *
     * @return this
     */
    public CommunicationManagerFactory enableDebugging() {
        this.debugging = true;
        return this;
    }

    /**
     * Disable security alerts in case there are no valid certificates.
     *
     * @return this
     */
    public CommunicationManagerFactory ignoreSecurityAlerts() {
        this.ignoreSecurityAlerts = true;
        return this;
    }


    /**
     * @return A communication manager produced using the given information
     */
    public CommunicationManager create() {
        String pathname = "pem";
        File cert = new File(pathname + File.separator + "cert.pem");
        File key = new File(pathname + File.separator + "privkey.pem");
        if(!cert.getParentFile().exists()) {
            if(!cert.getParentFile().mkdir()) {
                System.out.println("Failed to create certificate folder.");
            }
        }
        CommunicationHandler handler = new CommunicationHandler(conference, timeoutAfter, maxUserConnections, debugging);
        CommunicationManager manager = new NettyCommunicationManagerFactory(handler, port, cert, key).create();
        if(!manager.isSecure()) {
            if(debugging || ignoreSecurityAlerts) {
                System.out.println("[]----------[SECURITY ALERT]----------[]");
                System.out.println("No certificate found. Sever will be started in not encrypted debugging mode!");
            } else {
                System.out.println("Could not retrieve certificate. The server can not be started for security reasons.");
                System.out.println("To fix this issue provide valid cert and privkey files at following locations:");
                System.out.println("- " + cert.getPath());
                System.out.println("- " + key.getPath());
                System.exit(0);
            }
        }
        return manager;
    }


}
