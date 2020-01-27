package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import user.Attendee;
import user.SimpleAttendee;

import java.util.List;

/**
 * This packet can be used by an admin to add multiple attendee's to the conference at once. Responds with a {@link communication.packets.BasePacket}.
 * If an attendee can't be added, the valid ones are still added to the conference.
 */
public class AddMultipleAttendeesRequestPacket extends AuthenticatedRequestPacket {

    private List<SimpleAttendee> attendees;

    /**
     * @param attendees A list of {@link SimpleAttendee}'s, a raw data object for attendee data
     */
    public AddMultipleAttendeesRequestPacket(List<SimpleAttendee> attendees) {
        super(PacketType.ADD_MULTIPLE_ATTENDEES_REQUEST);
        this.attendees = attendees;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            attendees.forEach(a -> {
                if(a.getName() == null) {
                    throw new IllegalArgumentException();
                }
            });
            new ValidResponsePacket().send(connection);
            attendees.forEach(a -> {
                try {
                    Attendee attendee = new Attendee(a.getName(), a.getEmail(), conference.getFreeUserName(a.getName()), a.getGroup(), a.getResidence(), a.getFunction());
                    conference.addAttendee(attendee);
                } catch (Exception e) {

                }
            });
        }
    }
}
