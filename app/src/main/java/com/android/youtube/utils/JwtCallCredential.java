package com.android.youtube.utils;

import com.android.youtube.App;

import java.util.concurrent.Executor;

import io.grpc.Attributes;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

public class JwtCallCredential implements CallCredentials {


    @Override
    public void applyRequestMetadata(MethodDescriptor<?, ?> method, Attributes attrs, Executor appExecutor, MetadataApplier applier) {
        try {
            Metadata headers = new Metadata();
            Metadata.Key<String> jwtKey = Metadata.Key.of("token", Metadata.ASCII_STRING_MARSHALLER);
            headers.put(jwtKey, App.user.getToken());
            headers.put(Metadata.Key.of("user_id", Metadata.ASCII_STRING_MARSHALLER), App.user.getUserId()+"");
            headers.put(Metadata.Key.of("device_id", Metadata.ASCII_STRING_MARSHALLER), App.user.getDeviceId()+"");
            headers.put(Metadata.Key.of("request_id", Metadata.ASCII_STRING_MARSHALLER),System.currentTimeMillis()+"");
            applier.apply(headers);
        } catch (Throwable e) {
            applier.fail(Status.UNAUTHENTICATED.withCause(e));
        }
    }

    @Override
    public void thisUsesUnstableApi() {
    }
}