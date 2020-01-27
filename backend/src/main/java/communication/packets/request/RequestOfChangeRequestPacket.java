package communication.packets.request;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an attendee to submit a request of change.
 * Responds with a {@link communication.packets.BasePacket}.
 */
public class RequestOfChangeRequestPacket extends AuthenticatedRequestPacket {

    private boolean refersToTopic;
    private String reference;
    private String request;

    /**
     * @param refersToTopic if the request refers to a topic (false implies it refers to a document)
     * @param reference     in case the request refers to a topic the reference equals the preorder string of the topic, otherwise it equals the name of the referred document
     * @param request       the request of the attendee
     */
    public RequestOfChangeRequestPacket(boolean refersToTopic, String reference, String request) {
        super(PacketType.REQUEST_OF_CHANGE_REQUEST);
        this.refersToTopic = refersToTopic;
        this.reference = reference;
        this.request = request;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        new RequestOfPacketWrapper(getPacketType(), refersToTopic, reference, request).setToken(getToken()).handle(conference, connection);
    }
}
