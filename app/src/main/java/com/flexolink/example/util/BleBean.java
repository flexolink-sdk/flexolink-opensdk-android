package com.flexolink.example.util;

import java.io.Serializable;

/**
 * Description:
 * Created by huwei on 2022/11/7 14:26
 */
public class BleBean implements Serializable {
    /**
     * 设备名
     * */
    public String name;
    /**
     * 设备mac
     * */
    public String mac;
    public BleBean(String name, String mac){
        this.name = name;
        this.mac = mac;
    }
    /**
     * 获取蓝牙设备名
     * */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取蓝牙设备mac
     * */
    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
