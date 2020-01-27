package communication.connctiontests;

import com.google.gson.Gson;
import communication.enums.RequestResult;
import communication.packets.ResponsePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private AtomicBoolean success;
    private WebSocketClient client;

    public WebSocketClientHandler(final WebSocketClientHandshaker handshaker, AtomicBoolean success, WebSocketClient client) {
        this.handshaker = handshaker;
        this.success = success;
        this.client = client;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        cause.printStackTrace();

        if(!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if(!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            handshakeFuture.setSuccess();
            return;
        }


        WebSocketFrame frame = (WebSocketFrame) msg;
        if(frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            try {
                Gson gson = new Gson();
                ResponsePacket packet = gson.fromJson(textFrame.text(), ResponsePacket.class);
                if(packet.getResult() != RequestResult.Valid) {
                    throw new Exception();
                }
                success.set(true);
            } catch (Exception e) {
                success.set(false);
            }
        }

    }
}