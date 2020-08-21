package com.android.youtube.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;


// int32 type = 2; // 设备类型
//         string brand = 3; // 厂商
//         string model = 4; // 机型
//         string system_version = 5; // 系统版本
//         string sdk_version = 6; // sdk版本号
@Entity(indexes = {
        @Index(value = "id DESC", unique = true)
})
public class Devices {

    @Id
    private Long id;

    private int type;
    private String brand;
    private String model;
    private String system_version;
    private String sdk_version;
@Generated(hash = 336969467)
public Devices(Long id, int type, String brand, String model,
        String system_version, String sdk_version) {
    this.id = id;
    this.type = type;
    this.brand = brand;
    this.model = model;
    this.system_version = system_version;
    this.sdk_version = sdk_version;
}
@Generated(hash = 597445211)
public Devices() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public int getType() {
    return this.type;
}
public void setType(int type) {
    this.type = type;
}
public String getBrand() {
    return this.brand;
}
public void setBrand(String brand) {
    this.brand = brand;
}
public String getModel() {
    return this.model;
}
public void setModel(String model) {
    this.model = model;
}
public String getSystem_version() {
    return this.system_version;
}
public void setSystem_version(String system_version) {
    this.system_version = system_version;
}
public String getSdk_version() {
    return this.sdk_version;
}
public void setSdk_version(String sdk_version) {
    this.sdk_version = sdk_version;
}



}
