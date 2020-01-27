package communication.packets.request.admin;

import agenda.Agenda;
import agenda.Topic;
import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.BasePacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

import java.util.List;

/**
 * This packet can be used by an admin to add a topic to the agenda. Responds with a {@link BasePacket}.
 */
public class AddTopicRequestPacket extends AuthenticatedRequestPacket {

    private String position;
    private String name;

    /**
     * @param position the position of the topic as string (e.g. "1.4.3")
     * @param name     the name of the topic
     */
    public AddTopicRequestPacket(String position, String name) {
        super(PacketType.ADD_TOPIC_REQUEST);
        this.position = position;
        this.name = name;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Agenda mainAgenda = conference.getAgenda();
            Agenda agenda = mainAgenda.getAgendaFromPreorderString(position);
            Topic topic = new Topic(name, agenda);
            List<Integer> preorderList = agenda.getPreorderListFromPreorderString(position);
            //we assert the size of the preorderList to be at least one, otherwise a IllegalArgumenException would be thrown earlier
            int pos = preorderList.get(preorderList.size() - 1);
            if(!agenda.addTopic(topic, pos)) {
                throw new IllegalArgumentException();
            }
            new ValidResponsePacket().send(connection);
        }
    }
}
