package communication.packets.request.admin;

import agenda.Agenda;
import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.BasePacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to delete the complete Agenda at once. Responds with a {@link BasePacket}.
 */
public class DeleteAgendaRequestPacket extends AuthenticatedRequestPacket {

    /**
     *
     */
    public DeleteAgendaRequestPacket(String content) {
        super(PacketType.DELETE_AGENDA_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            conference.updateAgenda(new Agenda());
            new ValidResponsePacket().send(connection);
        }
    }
}
