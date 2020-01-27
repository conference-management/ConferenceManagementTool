package communication.packets;

import communication.enums.PacketType;
import communication.wrapper.Connection;
import main.Conference;

/**
 * An extension of {@link BasePacket} which handles requests of clients by running the {@link RequestPacket#handle(Conference, Connection)} method.
 */
public abstract class RequestPacket extends BasePacket {

    public RequestPacket(PacketType packetType) {
        super(packetType);
    }

    /**
     * A method which is called on any incoming packet.
     * Each subclass calls this method to handle it's associated request properly.
     *
     * @param conference An {@link Conference} which enables handling the request by calling provided backend methods.
     * @param connection An {@link Connection} to send an {@link ResponsePacket} to if required.
     */
    public abstract void handle(Conference conference, Connection connection);
}
