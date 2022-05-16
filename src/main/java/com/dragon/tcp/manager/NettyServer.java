package com.dragon.tcp.manager;


import com.dragon.tcp.protocol.MessageCodecSharable;
import com.dragon.tcp.protocol.ProcotolFrameDecoder;
import com.dragon.tcp.protocol.RpcRequestMessageHandler;
import com.dragon.tcp.util.ThreadPoolUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @author xuejingbao
 * @create 2021-12-21 14:34
 */
@Slf4j
public class NettyServer {

    private String serverAddress;
    private int serverPort;
    private CountDownLatch waitForServer;

    public NettyServer(String serverAddress, int serverPort, CountDownLatch waitForServer) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.waitForServer = waitForServer;
    }

    public void startNettyServer() {
        log.info("开始启动RPC-NettyServer端");
        EventLoopGroup bossGroup=new NioEventLoopGroup(1, ThreadPoolUtil.getThreadFactory());
        EventLoopGroup workGroup=new NioEventLoopGroup(2,ThreadPoolUtil.getThreadFactory());
        //日志
        LoggingHandler LOGGING = new LoggingHandler(LogLevel.DEBUG);
        //消息节码器
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //RPC请求处理器
        RpcRequestMessageHandler RPC_HANDLER = new RpcRequestMessageHandler();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast(new ProcotolFrameDecoder());//定长解码器
                            pipeline.addLast(LOGGING);
                            pipeline.addLast(MESSAGE_CODEC);
                            pipeline.addLast(RPC_HANDLER);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(this.serverAddress, this.serverPort).sync();
            log.info("RPC-NettyServer启动成功,端口是:{}", this.serverPort);
            waitForServer.countDown();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            log.error("RPC-NettyServer出现异常",e);
        }finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
