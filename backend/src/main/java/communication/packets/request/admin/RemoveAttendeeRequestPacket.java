package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.FailureResponsePacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to remove an attendee which is not an admin from the conference.
 */
public class RemoveAttendeeRequestPacket extends AuthenticatedRequestPacket {

    private int id;

    /**
     * @param id the id of the attendee to be removed
     */
    public RemoveAttendeeRequestPacket(int id) {
        super(PacketType.REMOVE_ATTENDEE_REQUEST);
        this.id = id;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            if(!conference.isAdmin(id)) {
                conference.removeAttendee(id);
                new ValidResponsePacket().send(connection);
            } else {
                new FailureResponsePacket("Admin accounts cant be removed").send(connection);
            }
        }
    }
}
