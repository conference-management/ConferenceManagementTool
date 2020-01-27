package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import user.Attendee;

/**
 * This packet can be used by an admin to add an attendee to the conference. Responds with a {@link communication.packets.BasePacket}.
 */
public class AddAttendeeRequestPacket extends AuthenticatedRequestPacket {

    private String name;
    private String email;
    private String group;
    private String residence;
    private String function;

    /**
     * The only compulsory parameter is name. If any other parameter is null
     * it is treated as if it was an empty string.
     *
     * @param name      the attendee's full name
     * @param email     the attendee's email
     * @param group     the attendee's group
     * @param residence the attendee's residence
     * @param function  the attendee's function
     */
    public AddAttendeeRequestPacket(String name, String email, String group, String residence, String function) {
        super(PacketType.ADD_ATTENDEE_REQUEST);
        this.name = name;
        this.email = email;
        this.group = group;
        this.residence = residence;
        this.function = function;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            if(name == null) {
                throw new IllegalArgumentException();
            }
            if(email == null) {
                email = "";
            }
            if(group == null) {
                group = "";
            }
            if(residence == null) {
                residence = "";
            }
            if(function == null) {
                function = "";
            }
            Attendee attendee = new Attendee(name, email, conference.getFreeUserName(name), group, residence, function);
            conference.addAttendee(attendee);
            new ValidResponsePacket().send(connection);
        }
    }
}
