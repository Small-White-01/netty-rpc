package com.rpccommon.demo.codec;

import com.rpccommon.demo.entity.Request;
import com.rpccommon.demo.util.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class JsonEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) throws Exception {
        String json = JSONUtil.serializeToJSON(obj);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(bytes);

    }
}
