# FlexolinkSdkExample

#### 介绍
柔灵科技SDK
http://openplatform.flexolinkai.com/

#### 软件架构
背景：柔灵科技专注于脑机接口技术的研发，为广大科研和企业用户提供脑机接口相关的API, 提供的服务有脑机接口设备的连接、脑电数据采集、存储、分析、睡眠闭环等相关接口。
目的：向对柔灵脑机接口Android SDk的客户提供二次开发的能力。


#### 快速接入SDK

#购买柔灵产品
开发者可联系业务人员购买柔灵肌电产品与脑电产品。
#生成 App Key
每个应用程序都需要一个唯一的应用程序密钥(App Key)来初始化SDK。
获取方式：请联系业务人员获取
#Android 示例代码
1、添加依赖库
在app模块build.gradle的dependencies中增加以下代码：

```
dependencies {
    	//柔灵SDK aar库
     implementation files('libs\\flex-sdk-v1.0.3-release.aar')
    	//巴斯滤波库
     implementation group: 'uk.me.berndporr', name:'iirj', version: '1.5'
    	//公共的数学函数库
     implementation 'org.apache.commons:commons-math3:3.6.1'
}

```
2、添加混淆规则
在增加app的proguard-rules.pro中增加：
```
-keepclasseswithmembers class flexolink.sdk.core.bleDeviceSdk.sdklib.ble.BleConnectFSM {
    <fields>;
    <methods>;
}
-keepclasseswithmembers class flexolink.sdk.core.bleDeviceSdk.sdklib.ble.BleScanFSM {
    <fields>;
    <methods>;
}
-keepclasseswithmembers class flexolink.sdk.core.natives.JNILogUtil {
    <fields>;
    <methods>;
}
-keepclasseswithmembers class flexolink.sdk.core.fsm.FsmEventManager {
    <fields>;
    <methods>;
}

-keep class flexolink.sdk.core.**{*;}
-keep class uk.me.berndporr.iirj.**{*;}
-keep class androidx.** {*;}
-keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}
```
3、在清单文件中添加apiKey和apiSetcret
在 Android 应用程序的清单文件（AndroidManifest.xml）中使用 <meta-data> 元素添加 API 密钥和 API 密钥秘密。如下：
```
<application
    ...
    >
    <meta-data
        android:name="apiKey"
        android:value="your_api_key_here" />
    <meta-data
        android:name="apiSecret"
        android:value="your_api_secret_here" />
    ...
</application>
```
4、获取授权
调用getAuth方法获取授权，获取授权成功后才能调用SDK的API。调用示例：
```
AppSDK.getInstance().getAuth(getApplicationContext(), new AuthorityInterface() {
    @Override
    public void onAuthSuccess() {
        //授权成功后，才可以使用其他API，可在调用成功后再调用初始化方法initSDK
        AppSDK.getInstance().initSDK(getApplicationContext());
    
    }

    @Override
    public void onAuthFailure(String msg) {
    	//授权失败
        //msg为失败原因
    }
});
```

#### demo 使用说明

1、使用 Android Studio Dolphin | 2021.3.1 Patch 1开发工具编译代码并运行。

2、demo界面如下：
![输入图片说明](https://foruda.gitee.com/images/1677482772841047761/2c53786f_762104.png "屏幕截图")

（1）点击“搜索”按钮搜索设备

（2）选择搜索到的设备

（3）点击“连接设备”按钮，连接成功后出现脑电图

![输入图片说明](https://foruda.gitee.com/images/1677482990289755745/4c0af569_762104.png "屏幕截图")

（4）点击“开始记录”按钮开始存储脑电数据

（5）数据采集完成后，点击“结束记录”，数据保存在用户指定的目录下。


