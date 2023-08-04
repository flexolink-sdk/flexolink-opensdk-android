package com.flexolink.example;


import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.flexolink.example.view.ScanBlePopWindow;
import com.github.mikephil.charting.charts.LineChart;
import com.flexolink.example.chart.ChartHelper;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import flexolink.sdk.core.AppSDK;
import flexolink.sdk.core.bean.SchemeInfoBean;
import flexolink.sdk.core.bean.SleepSchemeType;
import flexolink.sdk.core.bean.UserInfoBean;
import flexolink.sdk.core.bleDeviceSdk.sdklib.bean.BleBean;
import flexolink.sdk.core.bleDeviceSdk.sdklib.bean.ConnectResultType;
import flexolink.sdk.core.bleDeviceSdk.sdklib.bean.RecordEventCode;
import flexolink.sdk.core.bleDeviceSdk.sdklib.bean.ScanResultEvent;
import flexolink.sdk.core.bleDeviceSdk.sdklib.interfaces.ConnectListener;
import flexolink.sdk.core.bleDeviceSdk.sdklib.interfaces.ScanListener;
import flexolink.sdk.core.bleDeviceSdk.sdklib.utils.ToastUtil;
import flexolink.sdk.core.fsm.SleepListener;
import flexolink.sdk.core.fsm.SleepStageListener;
import flexolink.sdk.core.interfaces.AuthorityInterface;
import flexolink.sdk.core.interfaces.AuthorityListener;
import flexolink.sdk.core.interfaces.NoAuthException;
import flexolink.sdk.core.interfaces.RealTimeDataListener;
import flexolink.sdk.core.interfaces.RecordListener;
import flexolink.sdk.core.natives.NativeInterface;
import flexolink.sdk.core.util.JsonUtil;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    Button bt_search_patch, bt_connect_device, bt_start_record, bt_stop_record, bt_disconnect_device, bt_init, bt_release, bt_start_push, bt_stop_push;
    TextView tv_patch_name;
    TextView tv_init_sdk, tv_scan_device, tv_stop_scan, tv_connect_device, tv_get_connect_status, tv_real_data, tv_src_data, tv_close_device_connect, tv_is_wear_patch
            ,tv_get_device_battery, tv_signal_quality, tv_start_record, tv_stop_record, tv_add_event, tv_sleep_stage, tv_get_body_position, tv_get_rssi, tv_set_filter;

    Button bt_check;
    LineChart chart_eeg;
    long startTime ;
    int dataCount ;
    List<BleBean> bleBeanList = new ArrayList<>();
    ScanBlePopWindow scanBlePopWindow;
    boolean isPopWindowShowing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_search_patch = findViewById(R.id.bt_search_patch);
        tv_patch_name = findViewById(R.id.tv_patch_name);
        bt_connect_device = findViewById(R.id.bt_connect_device);
        bt_start_record = findViewById(R.id.bt_start_record);
        bt_stop_record = findViewById(R.id.bt_stop_record);
        bt_disconnect_device = findViewById(R.id.bt_disconnect_device);
        bt_init = findViewById(R.id.bt_init);
        bt_release = findViewById(R.id.bt_release);

        tv_init_sdk = findViewById(R.id.tv_init_sdk);
        tv_scan_device = findViewById(R.id.tv_scan_device);
        tv_stop_scan = findViewById(R.id.tv_stop_scan);
        tv_connect_device = findViewById(R.id.tv_connect_device);
        tv_get_connect_status = findViewById(R.id.tv_get_connect_status);
        tv_real_data = findViewById(R.id.tv_real_data);
        tv_src_data = findViewById(R.id.tv_src_data);
        tv_close_device_connect = findViewById(R.id.tv_close_device_connect);
        tv_is_wear_patch = findViewById(R.id.tv_is_wear_patch);
        tv_get_device_battery = findViewById(R.id.tv_get_device_battery);
        tv_signal_quality = findViewById(R.id.tv_signal_quality);
        tv_start_record = findViewById(R.id.tv_start_record);
        tv_stop_record = findViewById(R.id.tv_stop_record);
        tv_add_event = findViewById(R.id.tv_add_event);
        tv_sleep_stage = findViewById(R.id.tv_sleep_stage);
        tv_get_body_position = findViewById(R.id.tv_get_body_position);
        tv_get_rssi = findViewById(R.id.tv_get_rssi);
        tv_set_filter = findViewById(R.id.tv_set_filter);
        bt_check = findViewById(R.id.bt_check);
        bt_start_push = findViewById(R.id.bt_start_push);
        bt_stop_push = findViewById(R.id.bt_stop_push);
        bt_start_push.setOnClickListener(this);
        bt_stop_push.setOnClickListener(this);
        BleBean bleBean = SharedPrefUtils.getObject(getApplicationContext(), "pref_patch_ble", BleBean.class);
        if(bleBean != null){
            tv_patch_name.setText(bleBean.getName());
        }
        chart_eeg = findViewById(R.id.chart_eeg);
        ChartHelper.getInstance().initChart(this, handler,chart_eeg,true,5);
        bt_connect_device.setOnClickListener(this);
        bt_start_record.setOnClickListener(this);
        bt_stop_record.setOnClickListener(this);
        bt_disconnect_device.setOnClickListener(this);
        bt_init.setOnClickListener(this);
        bt_release.setOnClickListener(this);
        bt_search_patch.setOnClickListener(this);
        bt_check.setOnClickListener(this);
        AppSDK.getInstance().getAuth(getApplicationContext(), new AuthorityInterface() {
            @Override
            public void onAuthSuccess() {
                try {
                    AppSDK.getInstance().initSDK(getApplicationContext());
                } catch (NoAuthException e) {
                    e.printStackTrace();
                }
                AppSDK.getInstance().setRealDataListener(new RealTimeDataListener() {
                    @Override
                    public void onRealTimeData(float[] eegData) {
                        dataCount += eegData.length;
                        //统计数据量，定时统计
                        if(Math.abs(System.currentTimeMillis() - startTime) >= 1000){
                            startTime = System.currentTimeMillis();
                            Log.d(TAG, "收到数据个数 " + dataCount);
                            dataCount = 0;
                        }
                    }

                    @Override
                    public void onRealTimeFilterData(float[] eegData) {
                        //fsmTime = System.currentTimeMillis();
                        ChartHelper.getInstance().addMultiEntry(eegData);
                        //Log.d("耗时", "绘图耗时 " + (System.currentTimeMillis() - fsmTime) + "ms");
                    }
                });
                AppSDK.getInstance().setSleepStageListener(new SleepStageListener() {
                    @Override
                    public void onlineStage(int i) {
                        // 0 深睡 1 浅睡 2 REM 3 清醒
                        String stage[] = {"深睡", "浅睡", "REM", "清醒"};
                        Log.d(TAG, "分期结果： " + ((i >= 0 && i <= 3) ? stage[i] : "未知"));
                    }
                });
            }

            @Override
            public void onAuthFailure(String s) {
                runOnUiThread(()->{
                    Toast.makeText(getApplicationContext(), "未获取授权", Toast.LENGTH_SHORT).show();
                });

            }
        });

        initPopWindow();
    }
    void initPopWindow(){
        scanBlePopWindow = new ScanBlePopWindow(getApplicationContext(), bleBeanList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                scanBlePopWindow.dismiss();//关闭弹窗
                AppSDK.getInstance().stopScan();//停止扫描
                BleBean bleBean = bleBeanList.get(position);
                BleBean bleOld = SharedPrefUtils.getObject(getApplicationContext(), "pref_patch_ble", BleBean.class);
                if(bleOld != null && !bleOld.getName().equals(bleBean.getName())){
                    //更换额贴，需要先断开设备
                    SharedPrefUtils.putObject(getApplicationContext(), "pref_patch_ble" , bleBean);//存储起来
                    AppSDK.getInstance().closeDevice();
                }else {
                    SharedPrefUtils.putObject(getApplicationContext(), "pref_patch_ble", bleBean);//存储起来
                }
                //显示额贴设备名
                tv_patch_name.setText(bleBean.getName());
            }
        });
        scanBlePopWindow.setWidth(500);
        scanBlePopWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效
        scanBlePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.d(TAG, "pop 消失");
                isPopWindowShowing = false;
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        ChartHelper.getInstance().setShow(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChartHelper.getInstance().setShow(false);
    }

    @Override
    protected void onDestroy() {
        AppSDK.getInstance().release();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_search_patch:
                AppSDK.getInstance().stopScan();
                bleBeanList.clear();
                //调用设备搜索的接口
                int ret = AppSDK.getInstance().scanBleDevice(this, new ScanListener() {
                    @Override
                    public void onScanResult(BleBean bleBean) {
                        if(bleBean == null || TextUtils.isEmpty(bleBean.getName())) return;
                        if(bleBean.getName().contains("AirDream") || bleBean.getName().contains("Flex")){
                            Log.d(TAG, "扫描到设备 " + bleBean.getName());
                            if(!isExist(bleBeanList, bleBean)){
                                bleBeanList.add(bleBean);
                                if(scanBlePopWindow != null){
                                    scanBlePopWindow.setListRefresh(bleBeanList);
                                    if(!isPopWindowShowing){
                                        isPopWindowShowing = true;
                                        scanBlePopWindow.showAsDropDown(bt_search_patch, 0, -bt_search_patch.getHeight());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onScanFinish(ScanResultEvent scanResultEvent) {
                        Log.d(TAG, "扫描到结束");
                    }
                });
                if(ret == -1){
                    Toast.makeText(getApplicationContext(), "授权不通过", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_connect_device:
                if(!TextUtils.isEmpty(tv_patch_name.getText().toString())) {
                    SharedPrefUtils.putString(getApplicationContext(), "deviceName", tv_patch_name.getText().toString());
                    //扫描设备
                    int ret1 = AppSDK.getInstance().scanBleDevice(this, new ScanListener() {
                        @Override
                        public void onScanResult(BleBean bleBean) {
                            if(bleBean != null){
                                if(tv_patch_name.getText().toString().equals(bleBean.getName())){
                                    //关闭扫描
                                    AppSDK.getInstance().stopScan();
                                    //连接设备
                                    try {
                                        AppSDK.getInstance().connectBleDevice(MainActivity.this, bleBean.getName(), bleBean.getMac(), new ConnectListener() {
                                            @Override
                                            public void onConnectResult(ConnectResultType connectResultType) {
                                                if(connectResultType == ConnectResultType.SUCCESS){
                                                    Log.d("TAG", "连接设备成功 " + bleBean.getName());
                                                }else if(connectResultType == ConnectResultType.FAILURE){
                                                    Log.d("TAG", "连接设备失败 " + bleBean.getName());
                                                }else if(connectResultType == ConnectResultType.NULL){

                                                }
                                            }
                                        });
                                    } catch (NoAuthException e) {
                                        e.printStackTrace();
                                        runOnUiThread(()->{
                                            Toast.makeText(getApplicationContext(), "授权不通过", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onScanFinish(ScanResultEvent scanResultEvent) {

                        }
                    });
                    if(ret1 == -1){
                        Toast.makeText(getApplicationContext(), "授权不通过", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.bt_disconnect_device:
                AppSDK.getInstance().closeDevice();
                Toast.makeText(getApplicationContext(), "断开连接", Toast.LENGTH_SHORT ).show();
                break;
            case R.id.bt_start_record:
                String edf_path = getApplicationContext().getExternalFilesDir(null).getPath() + File.separator + System.currentTimeMillis() + ".edf";
                Log.d("TAG", "edf_path = " + edf_path);
                UserInfoBean userInfoBean = new UserInfoBean();
                userInfoBean.setName("huwei");
                userInfoBean.setBirthday("1990-10-23");
                userInfoBean.setSex(1);
                SchemeInfoBean schemeInfoBean = new SchemeInfoBean();
                schemeInfoBean.setSleepCnt(15);
                schemeInfoBean.setSleepScheme(SleepSchemeType.SCHEME_1.getValue() | SleepSchemeType.SCHEME_2.getValue());
                AppSDK.getInstance().startRecord(edf_path, userInfoBean,schemeInfoBean, new RecordListener() {
                    @Override
                    public void onStartRecord(String edfPath) {
                        Log.d("TAG", "记录开始 " + edfPath);
                        Toast.makeText(getApplicationContext(), "开始记录", Toast.LENGTH_SHORT ).show();
                    }
                    @Override
                    public void onRecording() {
                        Log.d("TAG", "正在记录");
                    }

                    @Override
                    public void onAutoStopRecord() {
                        Log.d("TAG", "自动停止记录");
                    }

                    @Override
                    public void onStopRecord() {
                        Log.d("TAG", "停止记录");
                        Toast.makeText(getApplicationContext(), "停止记录", Toast.LENGTH_SHORT ).show();
                    }

                    @Override
                    public void onRecordFailure(RecordEventCode recordEventCode) {
                        Log.d("TAG", "记录失败：" + recordEventCode.getMessage());
                    }
                });
                break;
            case R.id.bt_stop_record:
                AppSDK.getInstance().stopRecord();
                break;
            case R.id.bt_init:
                try {
                    AppSDK.getInstance().initSDK(getApplicationContext());
                } catch (NoAuthException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_release:
                AppSDK.getInstance().release();
                break;
            case R.id.bt_check:
                //检查权限
                checkAuth();
                break;
            case R.id.bt_start_push:
                AppSDK.getInstance().startPush(true);
                break;
            case R.id.bt_stop_push:
                AppSDK.getInstance().stopPush();
                break;
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:

                    break;
            }
        }
    };
    boolean isExist(List<BleBean> list, BleBean bleBean){
        if(list == null || bleBean == null){
            return false;
        }
        for (BleBean bean:list){
            if(bleBean.getMac().equals(bean.getMac())){
                return true;
            }
        }
        return false;
    }
    private void checkAuth(){
        try {
            AppSDK.getInstance().isConnected();
            showResult(tv_get_connect_status, true);
        } catch (NoAuthException e) {
            e.printStackTrace();
            showResult(tv_get_connect_status, false);
        }
        if(AppSDK.getInstance().pickDataByPointStamp(1) == null){
            showResult(tv_src_data, false);
        }else {
            showResult(tv_src_data, true);
        }
        try {
            AppSDK.getInstance().isWearPatch();
            showResult(tv_is_wear_patch, true);
        } catch (NoAuthException e) {
            e.printStackTrace();
            showResult(tv_is_wear_patch, false);
        }
        if(AppSDK.getInstance().getBattery() == -1){
            showResult(tv_get_device_battery, false);
        }else {
            showResult(tv_get_device_battery, true);
        }
        try {
            AppSDK.getInstance().signalQuality(new float[1], 1);
            showResult(tv_signal_quality, true);
        } catch (NoAuthException e) {
            e.printStackTrace();
            showResult(tv_signal_quality, false);
        }
        AppSDK.getInstance().addEvent(new Date(), new Date(), "测试");
        showResult(tv_add_event, AppSDK.getInstance().addEvent(new Date(), new Date(), "测试") == -1 ? false : true);
        showResult(tv_get_body_position, AppSDK.getInstance().getBodyPosition() == -1 ? false : true);
        showResult(tv_get_rssi, AppSDK.getInstance().getDeviceRssi() == -1 ? false : true);
        showResult(tv_set_filter, AppSDK.getInstance().setFilterParam(2.45, 45, 2) == -1 ? false : true);
    }
    private void showResult(TextView textView, boolean isAuth){
        if(isAuth){
            textView.setText("授权通过");
            textView.setTextColor(Color.GREEN);
        }else {
            textView.setText("授权不通过");
            textView.setTextColor(Color.RED);
        }
    }
}