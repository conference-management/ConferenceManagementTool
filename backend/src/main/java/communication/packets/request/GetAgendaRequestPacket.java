package communication.packets.request;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.GetAgendaResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an attendee to retrieve the agenda.
 * Responds with a {@link GetAgendaResponsePacket}.
 */
public class GetAgendaRequestPacket extends AuthenticatedRequestPacket {

    public GetAgendaRequestPacket() {
        super(PacketType.GET_AGENDA_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, false)) {
            new GetAgendaResponsePacket(conference.getAgenda()).send(connection);
        }
    }
}
