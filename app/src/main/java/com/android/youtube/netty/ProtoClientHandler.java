package com.android.youtube.netty;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.youtube.App;
import com.android.youtube.entity.DaoMaster;
import com.android.youtube.entity.DaoSession;
import com.android.youtube.entity.Message;
import com.android.youtube.entity.MessageDao;
import com.android.youtube.utils.DBUtils;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import pb.ConnExt;

public class ProtoClientHandler extends SimpleChannelInboundHandler<ConnExt.Output> {
    private static final String TAG = "ProtoClientHandler";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnExt.Output msg) throws Exception {
        Log.d(TAG, "收到服务器消息 "+msg+" ");
        if(msg.getType() == ConnExt.PackageType.PT_MESSAGE){
            ConnExt.Message message = ConnExt.Message.parseFrom(msg.getData());
            Log.i(TAG, "channelRead0: "+message);
            SQLiteDatabase writableDatabase = DBUtils.getInstance().getDbHelper().getWritableDatabase();
            DaoSession daoSession = new DaoMaster(writableDatabase).newSession();
            MessageDao messageDao = daoSession.getMessageDao();


            messageDao.insert(new Message());
        }
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
        this.ctx = ctx;
        ConnExt.SignInInput.Builder builder = ConnExt.SignInInput.newBuilder();

        ConnExt.SignInInput signInInput = builder
                .setDeviceId(9)
                .setToken(App.user.getToken())
                .setUserId(App.user.getUserId())
                .build();

        ConnExt.Input build = ConnExt.Input.newBuilder().setType(ConnExt.PackageType.PT_SIGN_IN)
                .setRequestId(System.currentTimeMillis())
                .setData(signInInput.toByteString()).build();
//        ChannelPromise promise = ctx.newPromise();
        ctx.writeAndFlush(build);

        Log.d(TAG, "与服务端连接成功："+App.user.getToken()+"  "+App.user.getUserId()+"  ");


//        ConnExt.SyncInput build = ConnExt.SyncInput.newBuilder().setSeq(1).build();
//        ctx.writeAndFlush(build);

    }

    private ChannelHandlerContext ctx;
    private ChannelPromise promise;

    public synchronized ChannelPromise sendMessage(Object message) {
        while (ctx == null) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
                //logger.error("等待ChannelHandlerContext实例化");
            } catch (InterruptedException e) {
            }
        }
        promise = ctx.newPromise();
        ctx.writeAndFlush(message);
        return promise;
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