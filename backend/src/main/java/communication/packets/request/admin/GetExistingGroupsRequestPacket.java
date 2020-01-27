package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.admin.GetExistingGroupsResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an admin to retrieve all existing groups i.e. a unique list of groups
 * which are assigned to at least one attendee. Respons with a {@link GetExistingGroupsRequestPacket}.
 */
public class GetExistingGroupsRequestPacket extends AuthenticatedRequestPacket {

    public GetExistingGroupsRequestPacket() {
        super(PacketType.GET_EXISTING_GROUPS_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Packet response;
            response = new GetExistingGroupsResponsePacket(conference.getExistingGroups());
            response.send(connection);
        }
    }
}
