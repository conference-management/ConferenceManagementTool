package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.admin.GetVotingsResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to retrieve all votings.
 * Responds with a {@link GetVotingsResponsePacket}.
 */
public class GetVotingsRequestPacket extends AuthenticatedRequestPacket {

    public GetVotingsRequestPacket() {
        super(PacketType.GET_VOTINGS_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Packet response = new GetVotingsResponsePacket(conference.getVotings());
            response.send(connection);
        }
    }
}
