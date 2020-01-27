import AuthenticatedRequestPacket from "../AuthenticatedRequestPacket.js";

export default class AddMultipleAttendeesRequestPacket extends AuthenticatedRequestPacket {

    constructor() {
        super("ADD_MULTIPLE_ATTENDEES_REQUEST");
        this.attendees = [];
    }

    addAttendee(name, email, group, residence, Function) {
        var attendee = {};
        attendee.name = name;
        attendee.email = email;
        attendee.group = group;
        attendee.residence = residence;
        attendee.function = Function;
        this.attendees.push(attendee);
    }
}
