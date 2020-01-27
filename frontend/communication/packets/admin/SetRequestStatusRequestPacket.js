import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class SetRequestStatusRequestPacket extends AuthenticatedRequestPacket {

    /**
     *
     * @param id the id of the request which status should be set
     * @param approved set's the approval status of the request, ignored in case if a SpeechRequest or if open = true
     * @param open set's if the request is still open
     */
    constructor(id, approved, open) {
        super("SET_REQUEST_STATUS_REQUEST");
        this.id = id;
        this.approved = approved;
        this.open = open;
    }
}
