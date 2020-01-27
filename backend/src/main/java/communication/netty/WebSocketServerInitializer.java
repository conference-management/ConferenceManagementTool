package communication.netty;

import communication.CommunicationHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.io.File;

/**
 * A websocket server which clients can use to communicate with the backend.
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final String WEBSOCKET_PATH = "/websocket";
    private static final String tempPath = System.getProperty("user.dir") + "/tmp/communication/";

    private final SslContext sslContext;
    private CommunicationHandler handler;

    /**
     * @param sslContext the sslContext to be used for secure communication
     * @param handler    the handler processing incoming messages
     */
    public WebSocketServerInitializer(SslContext sslContext, CommunicationHandler handler) {
        this.sslContext = sslContext;
        this.handler = handler;
        File tempFolder = new File(tempPath);
        if(tempFolder.exists()) {
            for(File f : tempFolder.listFiles()) {
                f.delete();
            }
        }
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if(sslContext != null) {
            pipeline.addLast(sslContext.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());

        //64 Kibibyte
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        //one Gibibyte
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true, 1073741824));
        pipeline.addLast(new WebSocketFrameHandler(handler, tempPath));
        pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(60 * 60));
    }
}