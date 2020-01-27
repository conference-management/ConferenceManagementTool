package communication.packets;

import communication.enums.PacketType;
import communication.wrapper.Connection;

/**
 * An interface representing a data packet which can be sent through an open {@link Connection} connection.
 */
public interface Packet {

    /**
     * @return The {@link PacketType} of the packet.
     */
    public PacketType getPacketType();

    /**
     * Sends this packet through an open {@link Connection}.
     *
     * @param connection the connection to send data through
     */
    public void send(Connection connection);
}
