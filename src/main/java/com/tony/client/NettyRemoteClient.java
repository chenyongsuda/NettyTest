package com.tony.client;

import com.tony.common.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Created by chnho02796 on 2017/10/31.
 */
public class NettyRemoteClient{

    private EventLoopGroup eventLoopGroupWorker;
    private Bootstrap bootstrap;
    private String host;
    private int port;
    private Channel channel;


    public void start(String host,int port) {
        this.host = host;
        this.port = port;
        try {
            bootstrap = new Bootstrap();
            eventLoopGroupWorker = new NioEventLoopGroup();
            Bootstrap handler = this.bootstrap.group(this.eventLoopGroupWorker)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, false)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .option(ChannelOption.SO_SNDBUF, 1024)
                    .option(ChannelOption.SO_RCVBUF, 1024)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            //Decode
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,0,2,0,2));
                            ch.pipeline().addLast(new MsgPackageDecoding());

                            //Encode
                            ch.pipeline().addLast(new LengthFieldPrepender(2));
                            ch.pipeline().addLast(new MsgPackageEncoding());
                            ch.pipeline().addLast(new com.tony.common.NettyConnectHandler());
                            ch.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            ChannelFuture f = this.bootstrap.connect(host, port).sync();
            this.channel = f.channel();
            this.channel.closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("Client link to server error host:"+host+" port:" + port);
        } finally {
            stop();
        }
    }

    public void SendRequest(String msg){
        try {
            this.channel.writeAndFlush(getSendByteBuf(msg)).sync();
        } catch (Exception e) {
            System.out.println("send message error");
            e.printStackTrace();
        }
    }

    private ByteBuf getSendByteBuf(String message)
            throws Exception {
        byte[] req = message.getBytes("UTF-8");
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(req);

        return pingMessage;
    }

    public void stop() {
        try {
            eventLoopGroupWorker.shutdownGracefully();
        } catch (Exception ex){
            System.out.println("Client stop error");
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        NettyRemoteClient client = new NettyRemoteClient();
        client.start("127.0.0.1",9000);
    }
}
