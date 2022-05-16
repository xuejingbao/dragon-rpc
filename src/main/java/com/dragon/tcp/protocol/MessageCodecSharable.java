package com.dragon.tcp.protocol;


import com.dragon.tcp.config.Config;
import com.dragon.tcp.constants.RpcConstant;
import com.dragon.tcp.core.Message;
import com.dragon.tcp.serial.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author xuejingbao
 * @create 2021-12-21 15:22
 */
@ChannelHandler.Sharable
@Slf4j
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    /*
    +----------------------------------------------+
    | 魔数 2byte | 序列化算法 1byte | 请求类型 1byte  |
    +----------------------------------------------+
    |            数据长度 4byte                      |
    +----------------------------------------------+
    */

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        log.info("=============RPC开始封装网络信息============");
        ByteBuf out = ctx.alloc().buffer();
        out.writeShort(RpcConstant.MAGIC); //写入魔数
        out.writeByte(Config.getSerializerAlgorithm().ordinal()); //写入序列化类型
        out.writeByte(msg.getMessageType());//为了反序列化对象用的
        byte[] data = Config.getSerializerAlgorithm().serialize(msg);
        out.writeInt(data.length); //写入消息长度
        out.writeBytes(data);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("==========RPC开始解析网络信息==============");
        log.info("收到信息："+ByteBufUtil.hexDump(in));
        in.markReaderIndex();//标记一个读取数据的索引，后续用来重置。
        short magic=in.readShort(); //读取magic
        if(magic!= RpcConstant.MAGIC){
            byteError(in);
            return;
        }
        byte serialType=in.readByte(); //读取序列化算法类型
        byte reqType=in.readByte(); //请求类型
        int dataLength=in.readInt(); //请求数据长度
        //可读区域的字节数小于实际数据长度
        if(in.readableBytes()<dataLength){
            in.resetReaderIndex();
            return;
        }
        //读取消息内容
        byte[] content = new byte[dataLength];
        in.readBytes(content);

        // 找到反序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serialType];
        // 确定具体消息类型
        Class<? extends Message> messageClass = Message.getMessageClass(reqType);
        Message message = algorithm.deserialize(messageClass, content);
        out.add(message);
    }

    private void byteError(ByteBuf in) {
        log.error("有不正常的数据进入：{}", ByteBufUtil.hexDump(in));
        in.release();
    }
}
