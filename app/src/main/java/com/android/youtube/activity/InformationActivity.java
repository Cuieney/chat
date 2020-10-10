package com.android.youtube.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.youtube.App;
import com.android.youtube.R;
import com.android.youtube.entity.User;
import com.android.youtube.image.ImageLoader;
import com.android.youtube.utils.NetworkUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pb.UserExtOuterClass;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class InformationActivity extends BaseActivity {

    private View userIdContainer;
    private View headContainer;
    private View nickNameContainer;
    private View back;
    private ImageView headImg;
    private TextView userName;
    private TextView userId;
    private User user;
    private String TAG = "InformationActivity";

    @Override
    public int setContentView() {
        return R.layout.activity_information;
    }

    @Override
    public void initView() {
        userIdContainer = findViewById(R.id.user_id_container);
        headContainer = findViewById(R.id.user_head_container);
        nickNameContainer = findViewById(R.id.nick_name_container);
        back = findViewById(R.id.back);
        headImg = ((ImageView) findViewById(R.id.headImg));
        userName = ((TextView) findViewById(R.id.nick_name));
        userId = ((TextView) findViewById(R.id.user_id));
    }

    @Override
    public void initData() {

        user = App.user;
        userId.setText(user.getUserId() + "");
        userName.setText(App.user.getUserName());
        userIdContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        headContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 0x110);
            }
        });
        nickNameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InformationActivity.this, UpdateInfoActivity.class));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x110 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            File file = new File(picturePath);

            compressImage(file.getAbsolutePath());

        }
    }


    @SuppressLint("CheckResult")
    private void updateImage(File file){
//        String base64 = encodeFileToBase64(file);
        UserExtOuterClass.UpdateUserReq userReq = UserExtOuterClass.UpdateUserReq.newBuilder()
                .setNickname(user.getUserName())
                .setAvatarUrl(file.getAbsolutePath())
                .build();
        NetworkUtils.getInstance().updateUserInfo(userReq)

                .subscribe(new Consumer<UserExtOuterClass.UpdateUserResp>() {
                    @Override
                    public void accept(UserExtOuterClass.UpdateUserResp updateUserResp) {
                        user.setUserImage(file.getAbsolutePath());
                        App.user = user;
                        ImageLoader.getInstance().load(file.getAbsolutePath()).into(headImg);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.i("updateUserInfo", "call: "+throwable);
                    }

                });
    }

    public  File saveImage( final String imageData) throws IOException {
        final byte[] imgBytesData = android.util.Base64.decode(imageData,
                android.util.Base64.DEFAULT);

        final File file = File.createTempFile("image", null, this.getCacheDir());
        final FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                fileOutputStream);
        try {
            bufferedOutputStream.write(imgBytesData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private  String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }
    private void compressImage(String path) {
        ArrayList<String> list = new ArrayList<>();
        list.add(path);
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(absolutePath.concat("/tmpImage/"));
        if (!file.exists()) {
            file.mkdir();
        }

        Luban.with(this)
                .load(list)
                .ignoreBy(100)
                .setTargetDir(file.getAbsolutePath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        Log.i(TAG, "onStart: ");
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        Log.i(TAG, "onSuccess: " + file.getAbsolutePath());

                        updateImage(file);


                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        Log.i(TAG, "onError: " + e.getMessage());

                    }
                }).launch();
    }
}
