package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to generate a new password for an attendee.
 * Note that generating a new password will not invalidate old tokens i.e. an logged in
 * attendee will not be logged out.
 * Responds with a {@link communication.packets.BasePacket}.
 */
public class GenerateNewAttendeePasswordRequestPacket extends AuthenticatedRequestPacket {

    int id;

    /**
     * @param id the id of the attendee for which a new password should be generated
     */
    public GenerateNewAttendeePasswordRequestPacket(int id) {
        super(PacketType.GENERATE_NEW_ATTENDEE_PASSWORD);
        this.id = id;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            conference.generateNewUserPassword(id);
            new ValidResponsePacket().send(connection);
        }
    }
}
