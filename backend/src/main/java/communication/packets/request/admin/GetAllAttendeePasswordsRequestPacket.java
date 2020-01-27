package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.admin.GetAllAttendeePasswordsResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to retrieve all attendees (users) passwords, including admins.
 * Responds with an {@link GetAttendeePasswordRequestPacket}.
 */
public class GetAllAttendeePasswordsRequestPacket extends AuthenticatedRequestPacket {

    public GetAllAttendeePasswordsRequestPacket() {
        super(PacketType.GET_ALL_ATTENDEE_PASSWORDS);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Packet response = new GetAllAttendeePasswordsResponsePacket(conference.getAllUsersPasswords());
            response.send(connection);
        }
    }
}
