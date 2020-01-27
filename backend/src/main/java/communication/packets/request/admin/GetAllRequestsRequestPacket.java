package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.admin.GetAllRequestsResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import request.Request;

import java.util.List;

/**
 * This packet can be used by an admin to retrieve all requests i.e. requests of speech and request of change at once.
 * Responds with an {@link GetAllRequestsResponsePacket}.
 */
public class GetAllRequestsRequestPacket extends AuthenticatedRequestPacket {

    public GetAllRequestsRequestPacket() {
        super(PacketType.GET_ALL_REQUESTS_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            List<Request> requests = conference.getAllRequests();
            new GetAllRequestsResponsePacket(requests).send(connection);
        }
    }
}
