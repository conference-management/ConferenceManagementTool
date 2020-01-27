package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.PersonalDataResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to retrieve the personal data of a single attendee.
 * Responds with a {@link PersonalDataResponsePacket}.
 */
public class GetAttendeeDataRequestPacket extends AuthenticatedRequestPacket {

    int id;

    /**
     * @param id the id of the attendee to retrieve the personal for
     */
    public GetAttendeeDataRequestPacket(int id) {
        super(PacketType.GET_ATTENDEE_DATA_REQUEST);
        this.id = id;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        Packet response = new PersonalDataResponsePacket(conference.getAttendeeData(id));
        response.send(connection);
    }
}
