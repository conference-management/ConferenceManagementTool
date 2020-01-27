package communication.packets.request.admin;

import agenda.Agenda;
import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.BasePacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to add the complete Agenda at once. Responds with a {@link BasePacket}.
 */
public class AddFullAgendaRequestPacket extends AuthenticatedRequestPacket {

    private String content;

    /**
     * @param content A String containing the topics in the correct order, split by newlines.
     *                Each topic consists of the dot-separated preOrder of that topic and its name, split by any other
     *                whitespace in  between.
     */
    public AddFullAgendaRequestPacket(String content) {
        super(PacketType.ADD_FULL_AGENDA_REQUEST);
        this.content = content;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            conference.updateAgenda(new Agenda(this.content));
            new ValidResponsePacket().send(connection);
        }
    }
}
