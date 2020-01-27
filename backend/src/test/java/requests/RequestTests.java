package requests;

import agenda.Agenda;
import agenda.Topic;
import main.Conference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import request.ChangeRequest;
import request.Request;
import request.SpeechRequest;
import user.Attendee;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.fail;

public class RequestTests {

    Conference conf;
    Attendee testAttendee;
    Attendee testAttendee2;

    @Before
    public void createConference() {
        Agenda agenda = new Agenda();
        Topic t = new Topic("Topic 1", agenda);
        agenda.addTopic(t, 0);
        conf = new Conference(true);
        conf.updateAgenda(agenda);
        testAttendee = new Attendee("test", "test", "test", "test", "test", "test");
        testAttendee2 = new Attendee("test na", "test@ttt", "test2", "test", "test", "test");
        conf.addAttendee(testAttendee);
        conf.addAttendee(testAttendee2);
    }

    @Test
    public void multipleRequests() {
        int requestCount = 20;

        for(int i = 0; i < requestCount; i++) {
            Request req = new ChangeRequest(testAttendee, conf.getAgenda().getTopic(0), System.currentTimeMillis(), "more " +
                    "tests");
            conf.addRequest(req);
        }

        int id = conf.getAllRequests().get(0).ID;
        ChangeRequest clone = (ChangeRequest) conf.getRequest(id).shallowClone();
        Assert.assertEquals(clone.getTimeStamp(), conf.getRequest(id).getTimeStamp());
        clone.approve();
        Assert.assertTrue(clone.isApproved());
        clone.reopen();
        Assert.assertTrue(clone.isOpen());
        clone.disapprove();
        Assert.assertFalse(clone.isOpen() && clone.isApproved());

        Assert.assertEquals("Not all requests got logged", requestCount, conf.getAllRequests().size());
    }

    @Test
    public void deleteTop() {
        Request req = new SpeechRequest(testAttendee, conf.getAgenda().getTopic(0), System.currentTimeMillis());
        SpeechRequest speech = (SpeechRequest) req.shallowClone();
        Assert.assertEquals(speech.getTimeStamp(), req.getTimeStamp());
        speech.close();
        Assert.assertFalse(speech.isOpen());
        speech.reopen();
        Assert.assertTrue(speech.isOpen());
        conf.addRequest(req);
        Agenda agenda = new Agenda();
        conf.updateAgenda(agenda);
        Assert.assertEquals("The request got removed", 1, conf.getAllRequests().size());
        Assert.assertEquals("Wrong request name", "1 Topic 1", conf.getAllRequests().get(0).getRequestable().getRequestableName());
    }

    @Test
    public void deleteDocument() {

        String pathString = "src/test/resources/test.txt";
        File f = new File(pathString);
        System.out.println(f.getAbsoluteFile());
        if(f.exists()) {
            f.delete();
        }

        try {
            f.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathString));
            writer.write("Test data\n");

            writer.close();
        } catch (IOException e) {
            fail("Could not initialize test environment");
        }

        conf.updateDocument("test.txt", "txt", f, true);
        Request req = new SpeechRequest(testAttendee, conf.getDocument("test.txt"), System.currentTimeMillis());
        conf.addRequest(req);
        Agenda agenda = new Agenda();
        conf.deleteDocument("test.txt");
        Assert.assertEquals("The request got removed", 1, conf.getAllRequests().size());
        Assert.assertEquals("Wrong request name", "test.txt", conf.getAllRequests().get(0).getRequestable().getRequestableName());
    }

    @Test
    public void deleteUser() {
        Request req = new SpeechRequest(testAttendee, conf.getAgenda().getTopic(0), System.currentTimeMillis());
        Request req2 = new SpeechRequest(testAttendee2, conf.getAgenda().getTopic(0), System.currentTimeMillis());
        conf.addRequest(req);
        conf.addRequest(req2);
        Agenda agenda = new Agenda();
        conf.removeAttendee(testAttendee.getID());
        Assert.assertEquals("The request got not removed", 1, conf.getAllRequests().size());
        Assert.assertEquals("Wrong user name", "1 Topic 1", conf.getAllRequests().get(0).getRequestable().getRequestableName());
    }

}
