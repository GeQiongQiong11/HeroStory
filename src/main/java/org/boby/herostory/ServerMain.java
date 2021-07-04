package org.boby.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author P0145348
 * @description: 服务器入口类
 * @date 2021/7/4/0004
 */
//@Slf4j
public class ServerMain {

    static final Logger LOGGER = LoggerFactory.getLogger(org.boby.herostory.ServerMain.class);

    static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        // 配置log4j
        PropertyConfigurator.configure(org.boby.herostory.ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup);
            // 服务器信道处理方式
            serverBootstrap.channel(NioServerSocketChannel.class);

            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(
                            new HttpServerCodec(),  //http服务器编解码器
                            new HttpObjectAggregator(65535), //内容长度限制
                            new WebSocketServerProtocolHandler("/websocket"), //WebSocket 协议处理器
                            new GameMsgHandler()  //自定义消息处理器
                    );
                }
            });

            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            //绑定端口号，实际项目中会使用argvArray中的参数指定端口号
            ChannelFuture future = serverBootstrap.bind(SERVER_PORT).sync();

            if(future.isSuccess()){
                LOGGER.info("游戏服务器启动成功");
            }

            //等待服务器信道关闭（即不要立即退出应用程序，让其可以一直提供服务，直到服务器信道关闭）
            future.channel().closeFuture().sync();
        }catch (Exception ex){
            LOGGER.error(ex.getMessage(), ex);
        }finally {
            //关闭服务器
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
