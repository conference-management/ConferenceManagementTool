package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.admin.GetAllAttendeesResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to retrieve the personal data of all attendees at once.
 * Responds with an {@link GetAllAttendeesResponsePacket} if the request was valid.
 */
public class GetAllAttendeesRequestPacket extends AuthenticatedRequestPacket {

    public GetAllAttendeesRequestPacket() {
        super(PacketType.GET_ALL_ATTENDEES_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Packet response = new GetAllAttendeesResponsePacket(conference.getAllAttendees());
            response.send(connection);
        }
    }
}
