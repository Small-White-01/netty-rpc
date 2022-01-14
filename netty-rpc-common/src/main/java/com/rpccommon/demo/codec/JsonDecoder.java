package com.rpccommon.demo.codec;

import com.rpccommon.demo.entity.Request;
import com.rpccommon.demo.util.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class JsonDecoder extends LengthFieldBasedFrameDecoder {
    public JsonDecoder() {
        super(65535, 0, 4,
               4, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decode = (ByteBuf) super.decode(ctx, in);
        if(decode==null)return null;
        int i = decode.readableBytes();
        byte[] bytes=new byte[i];
        decode.readBytes(bytes);
        String s = new String(bytes);

        return s;


    }
}
