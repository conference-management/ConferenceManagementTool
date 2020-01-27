package communication.netty;

import communication.CommunicationHandler;
import communication.wrapper.Connection;
import communication.wrapper.NettyConnectionWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Handles incoming messages by attaching frames of split messages and redirecting them to a {@link CommunicationHandler}.
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private CommunicationHandler handler;
    private StringBuilder textBuffer = null;
    private UUID uuid = null;
    private FrameType frameType = FrameType.Undefined;
    private String tempPath;
    /**
     * @param handler  the communication handler used for processing messages
     * @param tempPath the path of the tmp folder to store binary files in
     */
    public WebSocketFrameHandler(CommunicationHandler handler, String tempPath) {
        this.handler = handler;
        this.tempPath = tempPath;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws UnsupportedOperationException {
        Connection connection = new NettyConnectionWrapper(ctx);
        if(frame instanceof TextWebSocketFrame) {
            if(frameType == FrameType.Undefined) {
                frameType = FrameType.Text;
            } else {
                return;
            }

            String message = ((TextWebSocketFrame) frame).text();
            textBuffer = new StringBuilder(message);
        } else if(frame instanceof BinaryWebSocketFrame) {
            if(frameType == FrameType.Undefined && handler.mayTransferBinary(connection)) {
                frameType = FrameType.Binary;
            } else {
                return;
            }
            uuid = UUID.randomUUID();
            try {
                writeBytes(frame);
            } catch (Exception e) {
                frameType = FrameType.Undefined;
                return;
            }
        } else if(frame instanceof ContinuationWebSocketFrame) {
            ContinuationWebSocketFrame continuationWebSocketFrame = (ContinuationWebSocketFrame) frame;
            if(frameType == FrameType.Text) {
                textBuffer.append(continuationWebSocketFrame.text());
            } else if(frameType == FrameType.Binary) {
                try {
                    writeBytes(frame);
                } catch (Exception e) {
                    frameType = FrameType.Undefined;
                    return;
                }
            } else {
                throw new UnsupportedOperationException("unexpected continuation frame, no initial frame received");
            }
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
        if(frame.isFinalFragment()) {
            try {
                if(frameType == FrameType.Text) {
                    handler.onMessage(connection, textBuffer.toString());
                } else if(frameType == FrameType.Binary) {
                    File file = new File(tempPath + uuid.toString());
                    handler.onMessage(connection, file);
                }
            } finally {
                frameType = FrameType.Undefined;
                textBuffer = null;
                uuid = null;
            }
        }
    }

    private void writeBytes(WebSocketFrame frame) throws Exception {
        File f = new File(tempPath + uuid.toString());
        if(!f.getParentFile().exists()) {
            f.getParentFile().mkdir();
        }
        f.createNewFile();
        byte[] fileBytes = new byte[frame.content().readableBytes()];
        frame.content().readBytes(fileBytes);
        OutputStream os = new FileOutputStream(f, true);
        os.write(fileBytes, 0, fileBytes.length);
        os.close();
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        Connection connection = new NettyConnectionWrapper(ctx);
        handler.onRegistered(connection);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        Connection connection = new NettyConnectionWrapper(ctx);
        handler.onUnregistered(connection);
    }
}