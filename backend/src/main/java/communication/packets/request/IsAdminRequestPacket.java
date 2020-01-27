package communication.packets.request;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.Packet;
import communication.packets.response.IsAdminResponsePacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This packet can be used by an user to request if their account is an admin.
 * Responds with an {@link IsAdminResponsePacket}.
 */
public class IsAdminRequestPacket extends AuthenticatedRequestPacket {

    public IsAdminRequestPacket() {
        super(PacketType.IS_ADMIN_REQUEST);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, false)) {
            Packet response = new IsAdminResponsePacket(conference.isAdmin(conference.tokenToID(getToken())));
            response.send(connection);
        }
    }
}
