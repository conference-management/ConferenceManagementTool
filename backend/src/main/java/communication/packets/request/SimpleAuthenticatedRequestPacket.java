package communication.packets.request;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.wrapper.Connection;
import main.Conference;

/**
 * This class is used for parsing JSON to an {@link AuthenticatedRequestPacket} since the class is abstract
 * and cant be instantiated directly. This is not a packet which can be received explicitly.
 */
public class SimpleAuthenticatedRequestPacket extends AuthenticatedRequestPacket {

    public SimpleAuthenticatedRequestPacket(PacketType packetType) {
        super(packetType);
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        return;
    }
}
