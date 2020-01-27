package communication.packets.response.admin;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;

public class GetAttendeePasswordResponsePacket extends ResponsePacket {

    @Expose
    private String password;

    /**
     * @param password the password if the attendee
     */
    public GetAttendeePasswordResponsePacket(String password) {
        super(PacketType.GET_ATTENDEE_PASSWORD_RESPONSE, RequestResult.Valid);
        this.password = password;
    }
}
