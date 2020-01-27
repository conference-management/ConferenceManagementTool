import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class DeleteAgendaRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("DELETE_AGENDA_REQUEST");
    }
}