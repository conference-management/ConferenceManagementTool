package communication.packets.response;

import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;

/**
 * An object indicating that the token from the former request was invalid.
 */
public class InvalidTokenResponsePacket extends ResponsePacket {

    public InvalidTokenResponsePacket() {
        super(PacketType.INVALID_TOKEN, RequestResult.InvalidToken);
    }
}
