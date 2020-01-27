package communication.packets.request.admin;

import agenda.Agenda;
import agenda.Topic;
import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.BasePacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to reorder a topic of the agenda i.e. giving it a new position.
 * Responds with a {@link BasePacket}.
 */
@Deprecated
public class ReorderTopicRequestPacket extends AuthenticatedRequestPacket {

    private String oldPosition;
    private int newPosition;

    /**
     * @param oldPosition the old position of the topic as string (e.g. "1.4.3")
     * @param newPosition the new position of the topic in the (sub-)agenda
     */
    public ReorderTopicRequestPacket(String oldPosition, int newPosition) {
        super(PacketType.REORDER_TOPIC_REQUEST);
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Agenda agenda = conference.getAgenda();
            Topic topic = agenda.getTopicFromPreorderString(oldPosition);
            topic.reorder(newPosition);
            new ValidResponsePacket().send(connection);
        }
    }
}
