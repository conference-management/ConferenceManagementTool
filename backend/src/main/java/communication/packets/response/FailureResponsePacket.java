package communication.packets.response;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;

public class FailureResponsePacket extends ResponsePacket {

    @Expose
    private String details;

    public FailureResponsePacket() {
        this(null);
    }

    /**
     * @param details the details regarding the failure, null if there are none
     */
    public FailureResponsePacket(String details) {
        super(PacketType.FAILURE, RequestResult.Failure);
        this.details = details;
    }
}
