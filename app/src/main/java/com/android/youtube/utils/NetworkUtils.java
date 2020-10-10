package com.android.youtube.utils;

import com.android.youtube.netty.Const;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pb.LogicExtGrpc;
import pb.LogicExtOuterClass;
import pb.UserExtGrpc;
import pb.UserExtOuterClass;

public class NetworkUtils {
    private static NetworkUtils instance;
    private JwtCallCredential credentials;

    public synchronized static NetworkUtils getInstance() {
        synchronized (DBUtils.class) {
            if (instance == null) {
                instance = new NetworkUtils();
            }
        }
        return instance;
    }

    private NetworkUtils() {
        credentials = new JwtCallCredential();
    }

    public ManagedChannel getLoginChannel() {
        return ManagedChannelBuilder
                .forAddress(Const.LOGIC_EXT_HOST, Const.MSG_SOCKET_PORT)
                .usePlaintext()
                .build();
    }

    public ManagedChannel getUserChannel() {
        return ManagedChannelBuilder
                .forAddress(Const.USER_EXT_HOST, Const.USER_EXT_PORT)
                .usePlaintext()
                .build();
    }


    public LogicExtGrpc.LogicExtBlockingStub POST4Login() {

        ManagedChannel loginChannel = getLoginChannel();

        return LogicExtGrpc
                .newBlockingStub(loginChannel)
                .withCallCredentials(credentials);
    }

    public UserExtGrpc.UserExtBlockingStub POST4User() {
        ManagedChannel userChannel = getUserChannel();
        return UserExtGrpc.newBlockingStub(userChannel)
                .withCallCredentials(credentials);
    }

    public Observable<LogicExtOuterClass.SendMessageResp> sendMsg(LogicExtOuterClass.SendMessageReq messageReq) {
        final LogicExtGrpc.LogicExtBlockingStub stub = POST4Login();
        return Observable.just(messageReq)

                .map(new Function<LogicExtOuterClass.SendMessageReq, LogicExtOuterClass.SendMessageResp>() {
                    @Override
                    public LogicExtOuterClass.SendMessageResp apply(LogicExtOuterClass.SendMessageReq messageReq) {
                        return stub.sendMessage(messageReq);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        ((ManagedChannel) stub.getChannel()).shutdownNow();
                    }
                });
    }

    public Observable<LogicExtOuterClass.AddFriendResp> addFriend(LogicExtOuterClass.AddFriendReq req) {
        final LogicExtGrpc.LogicExtBlockingStub stub = POST4Login();
        return Observable.just(req)
                .map(new Function<LogicExtOuterClass.AddFriendReq, LogicExtOuterClass.AddFriendResp>() {
                    @Override
                    public LogicExtOuterClass.AddFriendResp apply(LogicExtOuterClass.AddFriendReq addFriendReq) {
                        return stub.addFriend(addFriendReq);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        ((ManagedChannel) stub.getChannel()).shutdownNow();
                    }
                });
    }

    public Observable<LogicExtOuterClass.RegisterDeviceResp> registerDevice(LogicExtOuterClass.RegisterDeviceReq req) {
        final LogicExtGrpc.LogicExtBlockingStub stub = LogicExtGrpc
                .newBlockingStub(getLoginChannel());
        return Observable.just(req)
                .map(new Function<LogicExtOuterClass.RegisterDeviceReq, LogicExtOuterClass.RegisterDeviceResp>() {
                    @Override
                    public LogicExtOuterClass.RegisterDeviceResp apply(LogicExtOuterClass.RegisterDeviceReq req) {
                        return stub.registerDevice(req);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        ((ManagedChannel) stub.getChannel()).shutdownNow();
                    }
                });
    }

    public Observable<LogicExtOuterClass.AgreeAddFriendResp> agreeAddFriend(LogicExtOuterClass.AgreeAddFriendReq req) {
        final LogicExtGrpc.LogicExtBlockingStub stub = POST4Login();
        return Observable.just(req)
                .map(new Function<LogicExtOuterClass.AgreeAddFriendReq, LogicExtOuterClass.AgreeAddFriendResp>() {
                    @Override
                    public LogicExtOuterClass.AgreeAddFriendResp apply(LogicExtOuterClass.AgreeAddFriendReq req) {
                        return stub.agreeAddFriend(req);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        ((ManagedChannel) stub.getChannel()).shutdownNow();
                    }
                });
    }

    public Observable<LogicExtOuterClass.GetFriendsResp> getFriends(LogicExtOuterClass.GetFriendsReq req) {
        final LogicExtGrpc.LogicExtBlockingStub stub = POST4Login();
        return Observable.just(req)
                .map(new Function<LogicExtOuterClass.GetFriendsReq, LogicExtOuterClass.GetFriendsResp>() {
                    @Override
                    public LogicExtOuterClass.GetFriendsResp apply(LogicExtOuterClass.GetFriendsReq req) {
                        return stub.getFriends(req);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        ((ManagedChannel) stub.getChannel()).shutdownNow();
                    }
                });
    }


    public Observable<UserExtOuterClass.SignInResp> signIn(UserExtOuterClass.SignInReq signInReq) {
        final UserExtGrpc.UserExtBlockingStub stub = UserExtGrpc.newBlockingStub(getUserChannel());
        return Observable.just(signInReq)
                .map(new Function<UserExtOuterClass.SignInReq, UserExtOuterClass.SignInResp>() {
                    @Override
                    public UserExtOuterClass.SignInResp apply(UserExtOuterClass.SignInReq req) {
                        return stub.signIn(req);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        ((ManagedChannel) stub.getChannel()).shutdownNow();
                    }
                });
    }

    public Observable<UserExtOuterClass.UpdateUserResp> updateUserInfo(UserExtOuterClass.UpdateUserReq req) {
        final UserExtGrpc.UserExtBlockingStub stub = POST4User();
        return Observable.just(req)
                .map(new Function<UserExtOuterClass.UpdateUserReq, UserExtOuterClass.UpdateUserResp>() {
                    @Override
                    public UserExtOuterClass.UpdateUserResp apply(UserExtOuterClass.UpdateUserReq req) {
                        return stub.updateUser(req);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        ((ManagedChannel) stub.getChannel()).shutdownNow();
                    }
                });
    }




}
