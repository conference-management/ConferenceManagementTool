package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.FailureResponsePacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to logout a single attendee which is not an admin i.e.
 * the password and token of this attendee will be invalidated. Responds with a {@link communication.packets.BasePacket}.
 */
public class LogoutAttendeeRequestPacket extends AuthenticatedRequestPacket {

    private int id;

    /**
     * @param id of the attendee to be logged out
     */
    public LogoutAttendeeRequestPacket(int id) {
        super(PacketType.LOGOUT_ATTENDEE_REQUEST);
        this.id = id;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            if(!conference.isAdmin(id)) {
                conference.logoutUser(id);
                new ValidResponsePacket().send(connection);
            } else {
                new FailureResponsePacket("Admins can't be logged out").send(connection);
            }
        }
    }
}
