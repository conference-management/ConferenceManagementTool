package communication.packets.request.admin;

import communication.enums.PacketType;
import communication.packets.AuthenticatedRequestPacket;
import communication.packets.response.ValidResponsePacket;
import communication.wrapper.Connection;
import main.Conference;
import request.ChangeRequest;
import request.Request;
import request.SpeechRequest;

/**
 * This packet can be used by an admin to
 */
public class SetRequestStatusRequestPacket extends AuthenticatedRequestPacket {

    private int id;
    private boolean approved;
    private boolean open;

    /**
     * @param id       the id of the request which status should be set
     * @param approved set's the approval status of the request, ignored in case if a SpeechRequest or if open = true
     * @param open     set's if the request is still open
     */
    public SetRequestStatusRequestPacket(int id, boolean approved, boolean open) {
        super(PacketType.SET_REQUEST_STATUS_REQUEST);
        this.id = id;
        this.approved = approved;
        this.open = open;
    }

    @Override
    public void handle(Conference conference, Connection connection) {
        if(isPermitted(conference, connection, true)) {
            Request request = conference.getRequest(id);
            if(open) {
                request.reopen();
            } else if(request instanceof SpeechRequest) {
                ((SpeechRequest) request).close();
            } else {
                ChangeRequest changeRequest = (ChangeRequest) request;
                if(approved) {
                    changeRequest.approve();
                } else {
                    changeRequest.disapprove();
                }
            }
        }
        new ValidResponsePacket().send(connection);
    }
}
