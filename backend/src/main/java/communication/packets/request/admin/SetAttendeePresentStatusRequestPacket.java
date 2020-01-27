package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import user.Attendee;

/**
 * This packet can be used by an admin to set the present status of an attendee.
 * Responds with a {@link communication.packets.BasePacket}.
 */
public class SetAttendeePresentStatusRequestPacket extends AuthenticatedRequestPacket {

    int id;
    boolean present;

    /**
     * @param id      the id of the attendee for which the present status should be updated
     * @param present if the attendee is present
     */
    public SetAttendeePresentStatusRequestPacket(int id, boolean present) {
        super(PacketType.SET_ATTENDEE_PRESENT_STATUS_REQUEST);
        this.id = id;
        this.present = present;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Attendee attendee = conference.getAttendeeData(id);
            attendee.setPresent(present);
            conference.setPresentValue(attendee.getUserName(), present);
            new ValidResponsePacket().send(connection);
        }
    }
}
