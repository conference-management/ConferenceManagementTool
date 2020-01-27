import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class AddAttendeeRequestPacket extends AuthenticatedRequestPacket {

    constructor(name, email, group, residence, Function) {
        super("ADD_ATTENDEE_REQUEST");
        this.email = email;
        this.name = name;
        this.group = group;
        this.residence = residence;
        this.function = Function;
    }
}
