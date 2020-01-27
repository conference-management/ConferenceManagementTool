package communication.netty;

/**
 * This enum contains different types of data a {@link io.netty.handler.codec.http.websocketx.WebSocketFrame} may contain.
 */
public enum FrameType {
    Binary, Text, Undefined;
}
