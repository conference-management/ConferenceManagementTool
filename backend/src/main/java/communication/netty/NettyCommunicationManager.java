package communication.netty;

import communication.CommunicationHandler;
import communication.CommunicationManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 * @see CommunicationManager
 */
public class NettyCommunicationManager implements CommunicationManager {

    private boolean started = false;
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(Math.max(32, Runtime.getRuntime().availableProcessors()));
    private Channel channel;
    private int port;
    private CommunicationHandler handler;
    private boolean secure = true;
    private SslContext sslContext;

    public NettyCommunicationManager(CommunicationHandler handler, int port) {
        this(handler, port, null);
        secure = false;
    }

    public NettyCommunicationManager(CommunicationHandler handler, int port, SslContext sslContext) {
        this.handler = handler;
        this.port = port;
        this.sslContext = sslContext;
    }

    @Override
    public synchronized void start() {
        if(!started) {
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        //.handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new WebSocketServerInitializer(sslContext, handler));

                channel = b.bind(port).sync().channel();
                started = true;
            } catch (Exception e) {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        } else {
            throw new IllegalStateException("Server already started.");
        }
    }

    @Override
    public synchronized void stop() {
        if(!started) {
            throw new IllegalStateException("Server is already shut down");
        }
        handler.stop();
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        started = false;
    }

    public synchronized boolean isSecure() {
        return secure;
    }
}