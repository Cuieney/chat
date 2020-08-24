package com.android.youtube.netty;

import android.util.Log;

import com.android.youtube.App;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import pb.ConnExt;

public class ProtoClientHandler extends SimpleChannelInboundHandler<ConnExt.Message> {
    private static final String TAG = "ProtoClientHandler";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnExt.Message msg) throws Exception {
        Log.d(TAG, "收到服务器消息 "+msg.getMessage().toString()+" ");
    }


    /**
     * 与服务端连接成功的回调
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.d(TAG, "与服务端连接成功：" + ctx.toString());

        ConnExt.SignInInput.Builder builder = ConnExt.SignInInput.newBuilder();

        ConnExt.SignInInput signInInput = builder.setDeviceId(9).setToken(App.signInResp.getToken()).setUserId(App.signInResp.getUserId()).build();

        ctx.writeAndFlush(signInInput);
    }

    /**
     * 与服务端断开的回调
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.d(TAG, "与服务端断开连接：" + ctx.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Log.d(TAG, "与服务端断开连接exce：" + ctx.toString()+" "+cause.getMessage());

    }
}