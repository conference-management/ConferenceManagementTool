import agenda.Agenda;
import agenda.Topic;
import com.google.gson.Gson;
import communication.packets.request.LoginRequestPacket;
import communication.packets.response.GetAgendaResponsePacket;
import org.junit.Test;

public class CommunicationTests {

    @Test
    public void justASample() {
        //LoginRequestPacket l = new LoginRequestPacket("theAnswer", "42");
        String s = "{\n" +
                "  \"username\": \"theAnswer\",\n" +
                "  \"password\": \"42\",\n" +
                "  \"packetType\": \"LOGIN_REQUEST\"\n" +
                "}";
        LoginRequestPacket l = new Gson().fromJson(s, LoginRequestPacket.class);
        System.out.println(l.toJson());
    }

    @Test
    public void testGetAgendaResponsePacket() {
        //convert this snipped to a test after adding a JSON comparator
        Agenda agenda = new Agenda();
        Topic t1 = new Topic("Topic 1", agenda);
        Topic t2 = new Topic("Topic 2", agenda);
        Topic t3 = new Topic("Topic 3", agenda);
        agenda.addTopic(t1, 0);
        agenda.addTopic(t2, 1);
        agenda.addTopic(t3, 2);
        Topic subTopic = new Topic("Subtopic of 2", t2.getSubTopics());
        t2.getSubTopics().addTopic(subTopic, 0);
        GetAgendaResponsePacket packet = new GetAgendaResponsePacket(agenda);
        System.out.println(packet.toJson());
    }
}
