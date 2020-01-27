package communication.packets.request;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.PersonalDataResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import user.Attendee;

/**
 * This packet can be used by an attendee to request their own personal data.
 * Responds with a {@link PersonalDataResponsePacket}.
 */
public class PersonalDataRequestPacket extends AuthenticatedRequestPacket {

    public PersonalDataRequestPacket() {
        super(PacketType.PERSONAL_DATA_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, false)) {
            Attendee attendee = conference.getAttendeeData(conference.tokenToID(getToken()));
            Packet response = new PersonalDataResponsePacket(attendee);
            response.send(connection);
        }
    }
}
