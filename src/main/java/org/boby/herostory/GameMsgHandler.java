package org.boby.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author P0145348
 * @description: 自定义消息处理器
 * @date 2021/7/4/0004
 */
@Slf4j
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if(channelHandlerContext == null || o == null){
            return;
        }

        log.info("收到客户端消息：{}", o);
    }
}
