package com.tony.server;

import com.tony.common.User;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;
import java.util.List;

/**
 * Created by chnho02796 on 2017/10/31.
 */

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf in = (ByteBuf) msg;
//        byte[] req = new byte[in.readableBytes()];
//        in.readBytes(req);
//        String body = new String(req,"utf-8");
//        System.out.println("收到客户端消息:"+body);
//        String time = "QUERY FOR TIME".equals(body) ? new Date(System.currentTimeMillis()).toString() : "Error Format\n";
//        ctx.writeAndFlush(Unpooled.copiedBuffer((time).getBytes()));
        User user = (User)msg;
        System.out.println(msg);
//        ctx.writeAndFlush(user);
    }

}
