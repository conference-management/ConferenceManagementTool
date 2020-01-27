package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to logout every attendee i.e. it invalidates all passwords and tokens of
 * attendees which are not admins. Responds with a {@link communication.packets.BasePacket}.
 */
public class LogoutAllAttendeesRequestPacket extends AuthenticatedRequestPacket {

    public LogoutAllAttendeesRequestPacket() {
        super(PacketType.LOGOUT_ALL_ATTENDEES);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            conference.logoutNonAdmins(true);
            new ValidResponsePacket().send(connection);
        }
    }
}
