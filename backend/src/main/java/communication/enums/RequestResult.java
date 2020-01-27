package communication.enums;

import communication.packets.ResponsePacket;

/**
 * These request results which are sent as part of a {@link ResponsePacket} have following meaning:
 * Valid: the former request was processed successful
 * InvalidToken: the former request could not be processed since the provided token was invalid
 * Failure: the former request could not be processed due to other reasons than those mentioned above
 */
public enum RequestResult {

    Valid, InvalidToken, Failure;
}
