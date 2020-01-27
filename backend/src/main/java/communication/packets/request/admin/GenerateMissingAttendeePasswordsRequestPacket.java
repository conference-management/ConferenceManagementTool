package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to generate passwords for accounts of attendees which
 * do not have a password yet. Responds with a {@link communication.packets.BasePacket}.
 */
public class GenerateMissingAttendeePasswordsRequestPacket extends AuthenticatedRequestPacket {

    public GenerateMissingAttendeePasswordsRequestPacket() {
        super(PacketType.GENERATE_MISSING_ATTENDEE_PASSWORDS);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            conference.generateAllMissingUserPasswords();
            new ValidResponsePacket().send(connection);
        }
    }
}
