import Packet from "./Packet.js";

export default class LoginRequestPacket extends Packet {

    constructor(username, password) {
        super("LOGIN_REQUEST");
        this.username = username;
        this.password = password;
    }
}