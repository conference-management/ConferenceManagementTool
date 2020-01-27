package communication.packets.request;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an attendee to submit a request of speech.
 * Responds with a {@link communication.packets.BasePacket}.
 */
public class RequestOfSpeechRequestPacket extends AuthenticatedRequestPacket {

    private boolean refersToTopic;
    private String reference;

    /**
     * @param refersToTopic if the request refers to a topic (false implies it refers to a document)
     * @param reference     in case the request refers to a topic the reference equals the preorder string of the topic, otherwise it equals the name of the referred document
     */
    public RequestOfSpeechRequestPacket(boolean refersToTopic, String reference) {
        super(PacketType.REQUEST_OF_SPEECH_REQUEST);
        this.refersToTopic = refersToTopic;
        this.reference = reference;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        new RequestOfPacketWrapper(getPacketType(), refersToTopic, reference).setToken(getToken()).handle(conference, connection);
    }
}
