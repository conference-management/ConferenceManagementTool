package communication.packets.response;

import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;

/**
 * A packet indicating that the former request was valid i.e. it was processed.
 * In general this packet has the general {@link PacketType#VALID_RESPONSE} but may
 * contain a special {@link PacketType} depending on the former request.
 */
public class ValidResponsePacket extends ResponsePacket {

    public ValidResponsePacket() {
        super(PacketType.VALID_RESPONSE, RequestResult.Valid);
    }

    public ValidResponsePacket(PacketType packetType) {
        super(packetType, RequestResult.Valid);
    }
}
