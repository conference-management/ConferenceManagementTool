package communication.packets.response;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;
import main.Conference;

public class GetConferenceDataResponsePacket extends ResponsePacket {

    @Expose
    private Conference conference;

    /**
     * @param conference an object containing following exposed properties of the conference
     *                   name: String - the name of the conference
     *                   organizer: String - the name of the organizer of the conference
     *                   long: startsAt - the unix epoch of the time the conference starts
     *                   long: endsAt - the unix epoch of the time the conference ends
     */
    public GetConferenceDataResponsePacket(Conference conference) {
        super(PacketType.CONFERENCE_DATA_RESPONSE, RequestResult.Valid);
        this.conference = conference;
    }
}
