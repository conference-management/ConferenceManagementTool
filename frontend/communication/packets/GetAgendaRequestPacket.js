import AuthenticatedRequestPacket from "./AuthenticatedRequestPacket.js";

export default class GetAgendaRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("GET_AGENDA_REQUEST");
    }
}