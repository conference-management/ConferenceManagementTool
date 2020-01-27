package database;

import org.junit.Test;
import request.ChangeRequest;
import request.DB_RequestManagement;
import request.Request;
import request.Requestable;
import request.SpeechRequest;
import user.Attendee;
import user.DB_UserManagement;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestManagerTests extends DatabaseTests {

    @Test
    public void addRequestTest() {
        DB_RequestManagement reqDb = this.getRequestDB();
        DB_UserManagement dbGen = this.getGeneralUserDB();
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        dbGen.addAttendee(max, "1234", "42");

        assertTrue(reqDb.addRequest(new ChangeRequest(1, max, new Requestable() {
            @Override
            public String getRequestableName() {
                return "Topic1";
            }
        }, 1, "Question1")));

        assertTrue(reqDb.addRequest(new SpeechRequest(2, max, new Requestable() {
            @Override
            public String getRequestableName() {
                return "Agenda1";
            }
        }, 1)));


    }

    @Test
    public void getvalidRequestTest() {
        DB_RequestManagement reqDb = this.getRequestDB();
        DB_UserManagement dbGen = this.getGeneralUserDB();
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        dbGen.addAttendee(max, "1234", "42");
        Request changerequest = new ChangeRequest(1, max, new Requestable() {
            @Override
            public String getRequestableName() {
                return "Topic1";
            }
        }, 1, "Question1");
        assertTrue(reqDb.addRequest(changerequest));

        Request speachrequest = new SpeechRequest(2, max, new Requestable() {
            @Override
            public String getRequestableName() {
                return "Agenda1";
            }
        }, 1);
        assertTrue(reqDb.addRequest(speachrequest));
        assertEquals(reqDb.getRequest(1).getRequester().getName(), changerequest.getRequester().getName());
        assertEquals(reqDb.getRequest(2).getTimeStamp(), speachrequest.getTimeStamp());

        //Test getAllRequests
        List<Request> requestlist = reqDb.getAllRequests();
        assertEquals(requestlist.get(0).getRequester().getID(), changerequest.getRequester().getID());
        assertEquals(requestlist.get(1).getTimeStamp(), changerequest.getTimeStamp());
    }

    @Test
    public void updateRequestTest() {
        DB_RequestManagement reqDb = this.getRequestDB();
        DB_UserManagement dbGen = this.getGeneralUserDB();
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        Attendee klaus = new Attendee("klaus Mustermann", "eail@email.muster", "Klaus.Mustermann", "LOL", "Place2", "Straßenkehrer", 1);
        dbGen.addAttendee(max, "1234", "42");
        dbGen.addAttendee(klaus, "1111", "9876");
        Request changerequest = new ChangeRequest(1, max, new Requestable() {
            @Override
            public String getRequestableName() {
                return "Topic1";
            }
        }, 1, "Question1");
        assertTrue(reqDb.addRequest(changerequest));

        Request speachrequest = new SpeechRequest(1, klaus, new Requestable() {
            @Override
            public String getRequestableName() {
                return "Agenda1";
            }
        }, 0);
        assertEquals(reqDb.getRequest(1).getRequester().getName(), changerequest.getRequester().getName());
        reqDb.update(speachrequest);
        assertEquals(reqDb.getRequest(1).getTimeStamp(), 0);
        assertEquals(reqDb.getRequest(1).getRequester().getName(), speachrequest.getRequester().getName());

        Request changerequest2 = new ChangeRequest(1, max, new Requestable() {
            @Override
            public String getRequestableName() {
                return "Topic2";
            }
        }, 1, "Question2");

        reqDb.update(changerequest2);
        assertEquals(reqDb.getRequest(1).getRequestable().getRequestableName(), "Topic2");
        assertEquals(((ChangeRequest) reqDb.getRequest(1)).getMessage(), "Question2");

    }

    @Test
    public void DeleteRequestTest() {
        DB_RequestManagement reqDb = this.getRequestDB();
        DB_UserManagement dbGen = this.getGeneralUserDB();
        Attendee max = new Attendee("Max Mustermann", "email@email.muster", "Max.Mustermann", "LOL", "Place", "Straßenkehrer", 0);
        dbGen.addAttendee(max, "1234", "42");

        reqDb.addRequest(new ChangeRequest(1, max, new Requestable() {
            @Override
            public String getRequestableName() {
                return "Topic1";
            }
        }, 1, "Question1"));

        reqDb.addRequest(new SpeechRequest(2, max, new Requestable() {
            @Override
            public String getRequestableName() {
                return "Agenda1";
            }
        }, 1));

        assertTrue(reqDb.removeRequest(max.getID()));
        assertTrue(reqDb.removeRequest(8));
        List<Request> requests = new LinkedList<>();
        assertEquals(reqDb.getAllRequests(), requests);


    }

}
