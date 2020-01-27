package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import user.Attendee;

/**
 * This packet can be used by an admin to edit the personal data of a user (internally an {@link Attendee} for both users and admins).
 * Responds with a {{@link communication.packets.BasePacket}}.
 */
public class EditUserRequestPacket extends AuthenticatedRequestPacket {

    private int id;
    private String name;
    private String email;
    private String group;
    private String residence;
    private String function;

    /**
     * @param id        the id of the user
     * @param name      the new name of the user
     * @param email     the new email of the user
     * @param group     the new group of the user
     * @param residence the new residence of the uer
     * @param function  the new function of the user
     */
    public EditUserRequestPacket(int id, String name, String email, String group, String residence, String function) {
        super(PacketType.EDIT_USER_REQUEST);
        this.id = id;
        this.name = name;
        this.email = email;
        this.group = group;
        this.residence = residence;
        this.function = function;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Attendee attendee = conference.getAttendeeData(id);
            if(name != null) {
                attendee.setName(name);
            }
            if(email != null) {
                attendee.setEmail(email);
            }
            if(group != null) {
                attendee.setGroup(group);
            }
            if(residence != null) {
                attendee.setResidence(residence);
            }
            if(function != null) {
                attendee.setFunction(function);
            }
            conference.editAttendee(attendee);
            new ValidResponsePacket().send(connection);
        }
    }
}
