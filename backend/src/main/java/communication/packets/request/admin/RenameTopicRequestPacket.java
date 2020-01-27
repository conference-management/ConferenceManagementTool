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
 * This packet can be used by an admin to rename a topic of the agenda. Responds with a {@link BasePacket}.
 */
public class RenameTopicRequestPacket extends AuthenticatedRequestPacket {

    private String position;
    private String name;

    /**
     * @param position the position of the topic as string (e.g. "1.4.3")
     * @param name     the new name for the topic
     */
    public RenameTopicRequestPacket(String position, String name) {
        super(PacketType.RENAME_TOPIC_REQUEST);
        this.position = position;
        this.name = name;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Agenda agenda = conference.getAgenda();
            Topic topic = agenda.getTopicFromPreorderString(position);
            topic.rename(name);
            new ValidResponsePacket().send(connection);
        }
    }
}
