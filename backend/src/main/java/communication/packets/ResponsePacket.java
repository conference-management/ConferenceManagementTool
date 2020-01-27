package communication.packets;


import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;

/**
 * A packet sent to clients as response to a {@link RequestPacket}.
 * If this packet itself is sent (i.e. not as an extended version) it still represents the packet type of another subclass
 * but just includes minimal information on the result of the former request.
 */
public class ResponsePacket extends BasePacket {

    @Expose
    private RequestResult result;

    public ResponsePacket(PacketType packetType, RequestResult result) {
        super(packetType);
        this.result = result;
    }

    public RequestResult getResult() {
        return result;
    }
}
