package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.admin.GetAttendeePasswordResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to retrieve the password of a single attendee.
 * Responds with a {@link GetAttendeePasswordRequestPacket}.
 */
public class GetAttendeePasswordRequestPacket extends AuthenticatedRequestPacket {

    int id;

    /**
     * @param id the attendee to retrieve the password for
     */
    public GetAttendeePasswordRequestPacket(int id) {
        super(PacketType.GET_ATTENDEE_PASSWORD_REQUEST);
        this.id = id;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        Packet response = new GetAttendeePasswordResponsePacket(conference.getUserPassword(id).second());
        response.send(connection);
    }
}
