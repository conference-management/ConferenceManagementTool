package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to generate a new token for an attendee.
 * If the attendee is logged in while this packet is handled they will be logged out since their old token
 * will be invalidated by generating a new one.
 * Responds with a {@link communication.packets.BasePacket}.
 */
public class GenerateNewAttendeeTokenRequestPacket extends AuthenticatedRequestPacket {

    int id;

    /**
     * @param id the id for the attendee for which a new token should be generated
     */
    public GenerateNewAttendeeTokenRequestPacket(int id) {
        super(PacketType.GENERATE_NEW_ATTENDEE_TOKEN);
        this.id = id;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            conference.generateNewUserToken(id);
            new ValidResponsePacket().send(connection);
        }
    }
}
