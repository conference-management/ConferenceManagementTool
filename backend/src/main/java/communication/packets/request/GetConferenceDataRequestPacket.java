package communication.packets.request;

import communication.enums.PacketType;
import communication.packets.Packet;
import communication.packets.RequestPacket;
import communication.packets.response.GetConferenceDataResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by anyone (no valid token required) to retrieve general information on the conference.
 * Responds with a {@link GetConferenceDataResponsePacket}.
 */
public class GetConferenceDataRequestPacket extends RequestPacket {

    public GetConferenceDataRequestPacket() {
        super(PacketType.CONFERENCE_DATA_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        Packet response = new GetConferenceDataResponsePacket(conference);
        response.send(connection);
    }
}
