package communication.packettests;

import com.google.gson.Gson;
import communication.CommunicationHandler;
import communication.packets.Packet;
import communication.packets.request.GetAgendaRequestPacket;
import communication.packets.request.LoginRequestPacket;
import communication.packets.request.PersonalDataRequestPacket;
import communication.packets.request.RequestOfChangeRequestPacket;
import communication.packets.request.RequestOfSpeechRequestPacket;
import communication.packets.request.admin.*;
import communication.utils.MoreAsserts;
import communication.wrapper.Connection;
import main.Conference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import request.ChangeRequest;
import request.Request;
import request.Requestable;
import request.SpeechRequest;
import user.Admin;
import user.Attendee;
import user.SimpleAttendee;
import user.User;
import voting.AnonymousVotingOption;
import voting.Voting;
import voting.VotingOption;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PacketTests {

    private Conference conference;
    private String adminToken;
    private String attendeeToken;
    private int adminID;
    private int attendeeID;
    private CommunicationHandler handler;

    @Before
    public void before() {
        conference = new Conference(true);
        for(User user : conference.getAllAttendees()) {
            if(conference.isAdmin(user.getID())) {
                conference.removeAdmin(user.getID());
            } else {
                conference.removeAttendee(user.getID());
            }
        }
        Admin admin = new Admin("admin", "admin", "admin", "admin", "admin", "admin");
        Attendee attendee = new Attendee("attendee", "attendee", "attendee", "attendee", "attendee", "attendee");
        conference.addAdmin(admin, "admin");
        conference.addAttendee(attendee, "attendee");
        adminToken = conference.login(admin.getUserName(), "admin").second().first();
        attendeeToken = conference.login(attendee.getUserName(), "attendee").second().first();
        adminID = conference.tokenToID(adminToken);
        attendeeID = conference.tokenToID(attendeeToken);
        handler = new CommunicationHandler(conference, 10, 10, false);
    }

    @Test
    public void testInvalidToken() {
        Connection connection = MoreAsserts.assertInvalidToken();
        handle(new PersonalDataRequestPacket(), connection);
        handle(new GetAllRequestsRequestPacket(), connection);
    }

    public void handle(Packet packet, Connection connection) {
        handler.onMessage(connection, new Gson().toJson(packet));
    }

    @Test
    public void testAddMultipleAttendeesRequestPacket() {
        int amount = 100;
        List<SimpleAttendee> attendees = new ArrayList<>();
        List<Attendee> expectedResult = new ArrayList<>();
        for(int i = 1; i <= amount; i++) {
            SimpleAttendee simpleAttendee = new SimpleAttendee("n" + (i % 3), "e" + ((i % 10 == 0) ? 10 : i), "g" + (i % 3), "r" + (i % 3), "f" + i);
            attendees.add(simpleAttendee);
            Attendee attendee = new Attendee("n" + (i % 3), "e" + ((i % 10 == 0) ? 10 : i), "XXXXXXXXX" + i, "g" + (i % 3), "r" + (i % 3), "f" + i);
            if(i % 10 != 0 || i == 10) {
                expectedResult.add(attendee);
            }
        }
        Connection connection = MoreAsserts.assertValidResult();
        handle(new AddMultipleAttendeesRequestPacket(attendees).setToken(adminToken), connection);
        removeBeforeUsers();
        MoreAsserts.assertUserListEquals(new ArrayList<>(expectedResult), new ArrayList<>(conference.getAllAttendees()));
    }

    public void removeBeforeUsers() {
        conference.removeAttendee(attendeeID);
        conference.removeAdmin(adminID);
    }

    @Test
    public void testAddTopicRequestPacket() {
        Connection connection = MoreAsserts.assertValidResult();
        handle(new AddTopicRequestPacket("1.", "1.").setToken(adminToken), connection);
        handle(new AddTopicRequestPacket("2.", "2.").setToken(adminToken), connection);
        handle(new AddTopicRequestPacket("1.1.", "1.1.").setToken(adminToken), connection);
        handle(new AddTopicRequestPacket("1.1.1.", "1.1.1.").setToken(adminToken), connection);
        handle(new AddTopicRequestPacket("3.", "4.").setToken(adminToken), connection);
        handle(new AddTopicRequestPacket("3.", "3.").setToken(adminToken), connection);
        String[] pos = {"1.", "2.", "1.1.", "1.1.1.", "3.", "4."};
        for(String s : pos) {
            Assert.assertEquals(s, conference.getAgenda().getTopicFromPreorderString(s).getName());
        }
    }

    /*
    @Test
    public void testEditUserRequestPacket_invalidNotEditable() {
        Connection connection = MoreAsserts.assertFailureResult();
        handle(new EditUserRequestPacket(adminID, "yes1","yes2","yes3","yes4","yes5").setToken(adminToken), connection);
        Assert.assertEquals("admin",conference.getAttendeeData(adminID).getName());
    }
    */

    @Test
    public void testAddTopicRequestPacketInvalidID() {
        Connection connection = MoreAsserts.assertFailureResult();
        handle(new AddTopicRequestPacket("3.", "3.").setToken(adminToken), connection);
        Assert.assertEquals(0, conference.getAgenda().getNumberOfTopics());
    }

    @Test
    public void testEditUserRequestPacket() {
        Connection connection = MoreAsserts.assertValidResult();
        handle(new EditUserRequestPacket(attendeeID, "yes1", "yes2", "yes3", "yes4", "yes5").setToken(adminToken), connection);
        Attendee attendee = conference.getAttendeeData(attendeeID);
        Assert.assertEquals("yes1", attendee.getName());
        Assert.assertEquals("yes2", attendee.getEmail());
        Assert.assertEquals("yes3", attendee.getGroup());
        Assert.assertEquals("yes4", attendee.getResidence());
        Assert.assertEquals("yes5", attendee.getFunction());
    }

    @Test
    public void testEditUserRequestPacket_invalidID() {
        Connection connection = MoreAsserts.assertFailureResult();
        handle(new EditUserRequestPacket(Integer.MAX_VALUE, "yes1", "yes2", "yes3", "yes4", "yes5").setToken(adminToken), connection);
        handle(new EditUserRequestPacket(42, "yes1", "yes2", "yes3", "yes4", "yes5").setToken(adminToken), connection);
    }

    @Test
    public void testLogoutAttendeeRequestPacket() {
        Connection connection = MoreAsserts.assertValidResult();
        String password = conference.getUserPassword(attendeeID).second();
        handle(new LogoutAttendeeRequestPacket(attendeeID).setToken(adminToken), connection);
        Connection connection2 = MoreAsserts.assertInvalidToken();
        handle(new GetAgendaRequestPacket().setToken(attendeeToken), connection2);
        Connection connection3 = MoreAsserts.assertFailureResult();
        handle(new LoginRequestPacket("attendee", password), connection3);
    }

    @Test
    public void testLogoutAttendeeRequestPacketInvalid() {
        Connection connection = MoreAsserts.assertFailureResult();
        String password = conference.getUserPassword(adminID).second();
        handle(new LogoutAttendeeRequestPacket(adminID).setToken(adminToken), connection);
        handle(new GetAllRequestsRequestPacket().setToken(adminToken), MoreAsserts.assertValidResult());
        Assert.assertEquals(password, conference.getUserPassword(adminID).second());
    }

    @Test
    public void testSetRequestStatusRequestPacket() {
        Connection connection = MoreAsserts.assertValidResult();
        handle(new AddTopicRequestPacket("1.", "1.").setToken(adminToken), connection);
        Requestable requestable = conference.getAgenda().getTopicFromPreorderString("1.");
        User requester = conference.getAttendeeData(attendeeID);
        ChangeRequest changeRequest = new ChangeRequest(requester, requestable, 0, "test");
        SpeechRequest speechRequest = new SpeechRequest(requester, requestable, 0);
        conference.addRequest(changeRequest);
        conference.addRequest(speechRequest);
        handle(new SetRequestStatusRequestPacket(changeRequest.getID(), true, true).setToken(adminToken), connection);
        Assert.assertTrue(!changeRequest.isApproved() && changeRequest.isOpen());
        handle(new SetRequestStatusRequestPacket(changeRequest.getID(), false, true).setToken(adminToken), connection);
        Assert.assertTrue(!changeRequest.isApproved() && changeRequest.isOpen());
        handle(new SetRequestStatusRequestPacket(changeRequest.getID(), true, false).setToken(adminToken), connection);
        Assert.assertTrue(changeRequest.isApproved() && !changeRequest.isOpen());
        handle(new SetRequestStatusRequestPacket(changeRequest.getID(), false, false).setToken(adminToken), connection);
        Assert.assertTrue(!changeRequest.isApproved() && !changeRequest.isOpen());

        handle(new SetRequestStatusRequestPacket(speechRequest.getID(), false, false).setToken(adminToken), connection);
        Assert.assertFalse(speechRequest.isOpen());
        handle(new SetRequestStatusRequestPacket(speechRequest.getID(), true, true).setToken(adminToken), connection);
        Assert.assertTrue(speechRequest.isOpen());
    }

    @Test
    public void testStartVotingRequestPacket() {
        List<VotingOption> options = new LinkedList<>();
        options.add(new AnonymousVotingOption(0, "1"));
        options.add(new AnonymousVotingOption(1, "2"));
        Voting voting = new Voting(options, "?", false, 60);
        conference.addVoting(voting);
        Connection connection = MoreAsserts.assertValidResult();
        handle(new StartVotingRequestPacket(voting.getID()).setToken(adminToken), connection);
        Assert.assertNotNull(conference.getActiveVoting());
    }

    @Test
    public void testStartVotingRequestPacketInvalid() throws Exception {
        Connection connection = MoreAsserts.assertFailureResult();
        Connection connection2 = MoreAsserts.assertValidResult();
        List<VotingOption> options = new LinkedList<>();
        options.add(new AnonymousVotingOption(0, "1"));
        options.add(new AnonymousVotingOption(1, "2"));
        Voting v1 = new Voting(options, "?", false, 1);
        Voting v2 = new Voting(options, "?", false, 1);
        conference.addVoting(v1);
        conference.addVoting(v2);
        conference.startVoting(v1);
        handle(new StartVotingRequestPacket(v1.getID()).setToken(adminToken), connection);
        handle(new StartVotingRequestPacket(v2.getID()).setToken(adminToken), connection);
        Thread.sleep(2000);
        List<VotingOption> options2 = new LinkedList<>();
        options2.add(new AnonymousVotingOption(0, "1"));
        Voting v3 = new Voting(options, "?", false, 0);
        Voting v4 = new Voting(options2, "?", false, 1);
        List<VotingOption> options3 = new LinkedList<>();
        options3.add(new AnonymousVotingOption(0, ""));
        options3.add(new AnonymousVotingOption(0, "12345"));
        Voting v5 = new Voting(options3, "?", false, 1);
        conference.addVoting(v3);
        conference.addVoting(v4);
        conference.addVoting(v5);
        handle(new StartVotingRequestPacket(v5.getID()).setToken(adminToken), connection);
        handle(new StartVotingRequestPacket(v3.getID()).setToken(adminToken), connection2);
        handle(new StartVotingRequestPacket(v4.getID()).setToken(adminToken), connection);
        handle(new StartVotingRequestPacket(42).setToken(adminToken), connection);
    }

    @Test
    public void testUpdateFileRequestPacketInvalid() {
        Connection connection = MoreAsserts.assertInvalidToken();
        Connection connection2 = MoreAsserts.assertClose();
        handle(new UpdateFileRequestPacket("test.pdf", "test.pdf", true).setToken(attendeeToken), connection);
        handler.onMessage(connection2, new File("./tmp/conference"));
    }

    @Test
    public void testRequestOfSpeechAndChange() {
        Connection connection = MoreAsserts.assertValidResult();
        handle(new AddTopicRequestPacket("1.", "1.").setToken(adminToken), connection);
        handle(new RequestOfSpeechRequestPacket(true, "1.").setToken(attendeeToken), connection);
        handle(new RequestOfChangeRequestPacket(true, "1.", "baguette").setToken(attendeeToken), connection);
        int i = 0;
        for(Request request : conference.getAllRequests()) {
            i++;
            if(request instanceof SpeechRequest) {
                Assert.assertEquals("attendee", ((SpeechRequest) request).getRequester().getName());
            } else {
                Assert.assertEquals("baguette", ((ChangeRequest) request).getMessage());
            }
        }
        Assert.assertEquals(2, i);
    }
}
