package communication.packets.response.admin;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;
import user.Attendee;

import java.util.List;

public class GetAllAttendeesResponsePacket extends ResponsePacket {

    /**
     *
     */
    @Expose
    private List<Attendee> attendees;

    public GetAllAttendeesResponsePacket(List<Attendee> attendees) {
        super(PacketType.GET_ALL_ATTENDEES_RESPONSE, RequestResult.Valid);
        this.attendees = attendees;
    }
}
