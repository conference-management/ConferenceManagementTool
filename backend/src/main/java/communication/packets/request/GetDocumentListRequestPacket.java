package communication.packets.request;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.GetDocumentListResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an attendee to retrieve a list of available files (documents).
 * Responds with a {@link GetDocumentListResponsePacket}.
 */
public class GetDocumentListRequestPacket extends AuthenticatedRequestPacket {

    public GetDocumentListRequestPacket() {
        super(PacketType.GET_DOCUMENT_LIST_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, false)) {
            Packet response = new GetDocumentListResponsePacket(conference.getAllDocuments());
            response.send(connection);
        }
    }
}
