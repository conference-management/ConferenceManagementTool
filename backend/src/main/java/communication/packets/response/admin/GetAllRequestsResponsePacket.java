package communication.packets.response.admin;

import com.google.gson.annotations.Expose;
import communication.enums.PacketType;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;
import request.Request;

import java.util.ArrayList;
import java.util.List;

public class GetAllRequestsResponsePacket extends ResponsePacket {

    @Expose
    List<Request> requests;

    /**
     * @param requests a list of all requests i.e. request of speech and request of change
     */
    public GetAllRequestsResponsePacket(List<Request> requests) {
        super(PacketType.GET_ALL_REQUESTS_RESPONSE, RequestResult.Valid);
        ArrayList<Request> result = new ArrayList<>();
        requests.forEach(r -> result.add(r.shallowClone())); //provides better requestables
        this.requests = result;
    }
}
