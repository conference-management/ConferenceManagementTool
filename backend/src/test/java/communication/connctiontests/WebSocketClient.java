package communication.connctiontests;

import com.google.gson.Gson;
import communication.packets.Packet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketClient {

    private final URI uri;
    EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;
    private AtomicBoolean running;
    private AtomicBoolean connected;
    private AtomicBoolean success;

    public WebSocketClient(int port) {
        running = new AtomicBoolean(false);
        connected = new AtomicBoolean(false);
        success = new AtomicBoolean(false);
        this.uri = URI.create("ws://localhost:" + port + "/websocket");
    }

    public synchronized void start() throws Exception {
        if(running.get()) {
            throw new IllegalStateException();
        }
        try {
            Bootstrap bootstrap = new Bootstrap();

            HttpHeaders customHeaders = new DefaultHttpHeaders();

            WebSocketClientHandler handler =
                    new WebSocketClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, false, customHeaders), success, this);

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("http-codec", new HttpClientCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(8192));
                            pipeline.addLast("ws-handler", handler);
                        }
                    });

            running.set(true);
            channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
            handler.handshakeFuture().sync();
            connected.set(true);
            channel.closeFuture().addListener((g) -> {
                connected.set(false);
            });
        } catch (Exception e) {
            running.set(false);
            connected.set(false);
        }
    }

    public synchronized void stop() {
        if(!running.get()) {
            throw new IllegalStateException();
        }
        channel.close();
        group.shutdownGracefully();
        running.set(false);
    }

    public synchronized void send(Packet packet) {
        Gson gson = new Gson();
        String json = gson.toJson(packet);
        send(json);
    }

    public synchronized void send(String msg) {
        ChannelFuture future = channel.writeAndFlush(new TextWebSocketFrame(msg));
    }

    public synchronized boolean isRunning() {
        return running.get();
    }

    public synchronized boolean isConnected() {
        return connected.get();
    }

    public synchronized Channel getChannel() {
        return channel;
    }

    public synchronized boolean isSuccessful() {
        return success.get();
    }
}