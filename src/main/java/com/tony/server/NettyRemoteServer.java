package com.tony.server;

import com.tony.client.NettyRemoteClient;
import com.tony.common.MsgPackageDecoding;
import com.tony.common.MsgPackageEncoding;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.net.InetSocketAddress;

/**
 * Created by chnho02796 on 2017/10/31.
 */
public class NettyRemoteServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bp;
    private int port;
    public void start(int port) {
        this.port = port;
        bossGroup   = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bp          = new ServerBootstrap();
        try {
            ServerBootstrap childHandler = bp.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .option(ChannelOption.SO_BACKLOG, 1024)
                            .childOption(ChannelOption.SO_KEEPALIVE, true)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel ch) throws Exception {
                                    //Decode
                                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,0,2,0,2));
                                    ch.pipeline().addLast(new MsgPackageDecoding());

                                    //Encode
                                    ch.pipeline().addLast(new LengthFieldPrepender(2));
                                    ch.pipeline().addLast(new MsgPackageEncoding());
                                    ch.pipeline().addLast(new NettyServerHandler());
                                }
                            });

            ChannelFuture f = bp.bind(port).sync();
            InetSocketAddress addr = (InetSocketAddress) f.channel().localAddress();
            this.port = addr.getPort();
            System.out.println("服务器开启："+port);
            f.channel().closeFuture().sync();
        } catch (Exception ex){
            System.out.println("Server start bind errors port:" + port);
        } finally {
            stop();
        }

    }

    public void stop() {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } catch (Exception ex){
            System.out.println("Server stop error" + port);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        NettyRemoteServer server = new NettyRemoteServer();
        server.start(9000);
    }
}
