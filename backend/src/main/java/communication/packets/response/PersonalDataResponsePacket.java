package communication.packets.response;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;
import communication.packets.request.PersonalDataRequestPacket;
import user.Attendee;

/**
 * A response containing personal data of an attendee which is sent as result of their {@link PersonalDataRequestPacket}.
 */
public class PersonalDataResponsePacket extends ResponsePacket {

    @Expose
    private Attendee attendee;

    /**
     * @param attendee the attendee who's data has been requested
     *                 Following properties of the attendee object are exposed:
     *                 name: String - the attendee's name
     *                 group: String - the attendee's group
     *                 function: String - the attendee's function in their group
     *                 email: String - the attendee's mail address
     *                 residence: String - the attendee's residence address
     *                 present: boolean - weather the attendee is present or not
     */
    public PersonalDataResponsePacket(Attendee attendee) {
        super(PacketType.PERSONAL_DATA_RESPONSE, RequestResult.Valid);
        this.attendee = attendee;
    }
}
