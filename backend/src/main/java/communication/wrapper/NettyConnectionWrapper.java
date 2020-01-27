package communication.wrapper;

import communication.packets.response.DownloadFileResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class NettyConnectionWrapper implements Connection {

    private ChannelHandlerContext context;

    public NettyConnectionWrapper(ChannelHandlerContext context) {
        this.context = context;
    }

    @Override
    public void send(String message) {
        context.writeAndFlush(new TextWebSocketFrame(message));
    }

    @Override
    public void sendBytes(byte[] bytes) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        System.out.println(byteBuf.readableBytes());
        sendBytes(byteBuf);
    }

    @Override
    public void sendFile(File file) {
        sendFile(file, file.getName());
    }

    private void sendBytes(ByteBuf byteBuf) {
        context.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    @Override
    public void sendFile(File file, String name) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            FileChannel fileChannel = fileInputStream.getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            ByteBuf byteBuf = Unpooled.wrappedBuffer(mappedByteBuffer);
            DownloadFileResponsePacket packet = new DownloadFileResponsePacket(null, name);
            ChannelFuture future = context.writeAndFlush(new TextWebSocketFrame(packet.toJson()));
            future.addListener((future1 -> {
                if(future.isDone() && future.isSuccess()) {
                    sendBytes(byteBuf);
                }
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendBytes(byte[] file, String name) {
        try {
            DownloadFileResponsePacket packet = new DownloadFileResponsePacket(null, name);
            ChannelFuture future = context.writeAndFlush(new TextWebSocketFrame(packet.toJson()));
            future.addListener((future1 -> {
                if(future.isDone() && future.isSuccess()) {
                    sendBytes(file);
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        context.close();
    }

    @Override
    public int hashCode() {
        return context.channel().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof NettyConnectionWrapper) {
            NettyConnectionWrapper o = (NettyConnectionWrapper) obj;
            return o.context.channel().equals(context.channel());
        }
        return false;
    }
}
