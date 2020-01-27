package communication;


import main.Conference;

/**
 * The interface that is responsible with waiting for requests and processing them. Receives a {@link Conference} which it uses to process requests
 */
public interface CommunicationManager {

    /**
     * Waits for requests and generates responses using the functionality provided by the {@link Conference}.
     * Should close the connection if completing the requests takes to long in order to prevent slow loris attacks.
     * What exactly to long means is specified by the init function
     */
    void start();

    /**
     * After calling this function the CommunicationManager stops processing new requests.
     */
    void stop();

    boolean isSecure();

}
