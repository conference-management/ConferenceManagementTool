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
 * This packet can be used by an admin to remove a topic from the agenda. Responds with a general {@link BasePacket}.
 */
public class RemoveTopicRequestPacket extends AuthenticatedRequestPacket {

    private String position;

    /**
     * @param position the position of the topic as string (e.g. "1.4.3")
     */
    public RemoveTopicRequestPacket(String position) {
        super(PacketType.REMOVE_TOPIC_REQUEST);
        this.position = position;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Agenda agenda = conference.getAgenda();
            Topic topic = agenda.getTopicFromPreorderString(position);
            topic.remove();
            new ValidResponsePacket().send(connection);
        }
    }
}
