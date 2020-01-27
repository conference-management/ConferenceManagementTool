import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class AddFullAgendaRequestPacket extends AuthenticatedRequestPacket {

    constructor(content) {
        super("ADD_FULL_AGENDA_REQUEST");
        this.content = content;
    }
}