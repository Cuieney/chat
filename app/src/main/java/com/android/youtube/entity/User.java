package com.android.youtube.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity(indexes = {
        @Index(value = "id DESC", unique = true)
})
public class User {
    @Id
    private Long id;
    private String userName;
    private String userPhone;
    private String token;
    private String userImage;
    private int userId;
    private int deviceId;
@Generated(hash = 1485938269)
public User(Long id, String userName, String userPhone, String token,
        String userImage, int userId, int deviceId) {
    this.id = id;
    this.userName = userName;
    this.userPhone = userPhone;
    this.token = token;
    this.userImage = userImage;
    this.userId = userId;
    this.deviceId = deviceId;
}
@Generated(hash = 586692638)
public User() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getUserName() {
    return this.userName;
}
public void setUserName(String userName) {
    this.userName = userName;
}
public String getUserPhone() {
    return this.userPhone;
}
public void setUserPhone(String userPhone) {
    this.userPhone = userPhone;
}
public String getToken() {
    return this.token;
}
public void setToken(String token) {
    this.token = token;
}
public int getUserId() {
    return this.userId;
}
public void setUserId(int userId) {
    this.userId = userId;
}
public int getDeviceId() {
    return this.deviceId;
}
public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
}
public String getUserImage() {
    return this.userImage;
}
public void setUserImage(String userImage) {
    this.userImage = userImage;
}

    

}
