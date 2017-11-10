package com.tony.client;

import com.tony.common.User;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;

/**
 * Created by chnho02796 on 2017/10/31.
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        User user = new User();
        user.setId("CHNHO00001");
        user.setName("Tony");
        for (int i = 0; i < 100; i++){
            ctx.writeAndFlush(user);
        }
        System.out.println("send finish");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] resp = new byte[buf.readableBytes()];
//        buf.readBytes(resp);
//        String str = new String(resp,"UTF-8");
        System.out.println(msg);
    }


}
