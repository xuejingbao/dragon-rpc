package com.dragon.tcp.manager;

import com.dragon.business.pojo.Address;
import com.dragon.business.server.NodeManagement;
import com.dragon.tcp.core.RpcRequestMessage;
import com.dragon.tcp.protocol.MessageCodecSharable;
import com.dragon.tcp.protocol.ProcotolFrameDecoder;
import com.dragon.tcp.protocol.RpcResponseMessageHandler;
import com.dragon.tcp.util.ThreadPoolUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xuejingbao
 * @create 2021-12-22 14:33
 */
@Slf4j
public class NettyClient {

    public static volatile NettyClient instance;

    private Bootstrap bootstrap;
    public static EventLoopGroup eventLoopGroup = new NioEventLoopGroup(3, ThreadPoolUtil.getThreadFactory());;
    private Map<String, Channel> nodeChannelMap;
    //根据序号key来判断是哪个请求的消息      value是用来接收结果的 promise 对象
    public static Map<Long, Promise<Object>> promiseMap = new ConcurrentHashMap<>();

    private static final AtomicLong id = new AtomicLong(1);
    public static Long nextId() {
        return id.incrementAndGet();
    }

    private NettyClient() {
        bootstrap = new Bootstrap();
        nodeChannelMap = new ConcurrentHashMap<>();
        bootstrapInit();
    }

    public static NettyClient getInstance(){
        if (instance == null){
            synchronized (NettyClient.class){
                if (instance == null){
                    instance = new NettyClient();
                }
            }
        }
        return instance;
    }

    private void bootstrapInit() {
        //日志handler
        LoggingHandler LOGGING = new LoggingHandler(LogLevel.DEBUG);
        //消息处理handler
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //处理相应handler
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        //心跳处理器
        //HeartBeatClientHandler HEATBEAT_CLIENT = new HeartBeatClientHandler();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //ch.pipeline().addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new ProcotolFrameDecoder());
                        ch.pipeline().addLast(LOGGING);
                        ch.pipeline().addLast(MESSAGE_CODEC);
                        ch.pipeline().addLast(RPC_HANDLER);
                    }
                });
    }

    public Channel getChannelByeNode(String node) throws Exception {
        if (nodeChannelMap.get(node) != null) {
            return nodeChannelMap.get(node);
        }
        Address address = NodeManagement.nodeAddressRelevance.get(node);
        if (address == null){
            //todo 这里似乎需要再从center中问一问,如果有该地址的信息了呢
            throw new Exception("该设备的IP信息未找到");
        }
        ChannelFuture connect = bootstrap.connect(address.getIp(), address.getPort()).sync();
        if (connect.isSuccess()) {
            Channel channel = connect.channel();
            nodeChannelMap.put(node,channel);
            return channel;
        }
        return null;
    }

    public void sendRequest(RpcRequestMessage rpcRequestMessage, String node) throws Exception {
        log.info("begin transfer data");
        getChannelByeNode(node).writeAndFlush(rpcRequestMessage);
    }

}
