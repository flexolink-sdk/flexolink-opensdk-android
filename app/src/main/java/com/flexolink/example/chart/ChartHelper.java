package com.flexolink.example.chart;


import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.flexolink.example.R;
import com.flexolink.example.util.FloatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 线图控制类
 * create by hw on 2021/10/16
 **/
public class ChartHelper {
    private static final String TAG = "ChartHelper";
    public static final int ONE_SECOND_DATA_NUM = 125; //一秒数据点的数量
    private final int Y_DEFAULT_AXIS_MINIMUM = -50;//y坐标默认最小值
    private final int Y_DEFAULT_AXIS_MAXIMUM = 50;//y坐标默认最大值
    public final static int DEFAULT_X_MAX_VALUE = 5;//默认x轴显示的最大秒数
    private int maxCount = ONE_SECOND_DATA_NUM * DEFAULT_X_MAX_VALUE;
    private LineChart mLineChart;
    private List<Entry> entryList = new ArrayList<>();
    private static ChartHelper instance;
    private Handler handler;
    private Activity activity;
    private boolean isShow = false;

    private int lineColor;
    private ChartHelper(){}
    public static ChartHelper getInstance(){
        if(instance == null){
            instance = new ChartHelper();
        }
        return instance;
    }
    public void initChart(Activity activity, Handler handler, LineChart lineChart, boolean isShow, int xMaxValue) {
        mLineChart = lineChart;
        this.handler = handler;
        this.isShow = isShow;
        this.activity = activity;
        maxCount = xMaxValue * ONE_SECOND_DATA_NUM;
        lineColor = activity.getResources().getColor(R.color.black);
        int textColor = Color.parseColor("#6E6E6E");
        int gridColor = Color.parseColor("#E1E1E1");
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);
        mLineChart.getDescription().setEnabled(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.getXAxis().setEnabled(false);//x轴坐标线是否显示
        mLineChart.getAxisLeft().setEnabled(false);//垂直方向刻度是否显示
        mLineChart.setTouchEnabled(false);
        mLineChart.setNoDataText("no data");
        mLineChart.setNoDataTextColor(textColor);
        LineData data = new LineData();
        data.setValueTextColor(lineColor);
        mLineChart.setData(data);
        // set custom chart offsets (automatic offset calculation is hereby disabled)
        mLineChart.setViewPortOffsets(0, 0, 0, 0);
        YAxis axisLeft = mLineChart.getAxisLeft();
        axisLeft.setAxisMinimum(Y_DEFAULT_AXIS_MINIMUM);
        axisLeft.setLabelCount(5,true);
        axisLeft.setDrawGridLines(false);//y轴轴线是否显示
        axisLeft.setDrawLabels(false);//y轴lable是否显示
        axisLeft.setAxisMaximum(Y_DEFAULT_AXIS_MAXIMUM);
        axisLeft.setGridColor(gridColor);
        axisLeft.setTextColor(textColor);
        axisLeft.setAxisLineColor(gridColor);
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setDrawGridLines(false);//x轴网格线是否显示
        xAxis.setDrawLabels(false);//x轴lable是否显示
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setAxisMinimum(0f);
       // xAxis.setAxisMaximum(maxCount);
        if(xMaxValue < 10){
            xAxis.setLabelCount(xMaxValue + 1,true);//强制label显示的数量
        }else {
            xAxis.setLabelCount(DEFAULT_X_MAX_VALUE + 1,true);//强制label显示的数量
        }
        xAxis.setAxisLineColor(gridColor);
        xAxis.setTextColor(textColor);
        xAxis.setGridColor(gridColor);
        EEGXValueFormatter customXValueFormatter = new EEGXValueFormatter(ONE_SECOND_DATA_NUM);
        xAxis.setValueFormatter(customXValueFormatter);
    }
    long sta = 0;
    public void addEntry(float yValue) {
        if((System.currentTimeMillis() - sta) > 30000){
            sta = System.currentTimeMillis();
            Log.d("wave", "showWave");
        }
        if(mLineChart == null) return;
        if(activity != null){
            activity.runOnUiThread(()->{
                LineData data = mLineChart.getData();
                if (data != null) {
                    //Log.d(TAG, "yValue = " + yValue);
                    LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                    if (set == null) {
                        set = createSet();
                        data.addDataSet(set);
                    }
                    //如果缓冲区种有数据，就把缓冲区的数据放进data
                    if(entryList.size() > 0){
                        for (int i = 0; i < entryList.size(); i++){
                            Entry entry = entryList.get(i);
                            entry.setX(i);
                            data.addEntry(entry, 0);
                        }
                        entryList.clear();
                    }
                    data.addEntry(new Entry(data.getEntryCount(), yValue), 0);
                    data.notifyDataChanged();

                    // let the chart know it's data has changed
                    mLineChart.notifyDataSetChanged();

                    // limit the number of visible entries
                    mLineChart.setVisibleXRangeMaximum(maxCount);
                    mLineChart.setVisibleXRangeMinimum(maxCount);

                    // move to the latest entry
                    mLineChart.moveViewToX(data.getEntryCount());
                    //Log.d(TAG, "长度 = " + data.getEntryCount());

                    //如果数据累计过多就清除，把一屏幕的数据放进缓冲区，备用
                    if(data.getEntryCount() > maxCount * 50){
                        entryList.clear();
                        for(int i = data.getEntryCount() - maxCount; i < data.getEntryCount(); i++){
                            entryList.add(data.getDataSetByIndex(0).getEntryForIndex(i));
                        }
                        mLineChart.clearValues();
                    }
                }
            });

        }

    }

    public void addMultiEntry(List<Float> yValues) {
        List<Float> _yValues = new ArrayList<>();
        _yValues.addAll(yValues);
        if((System.currentTimeMillis() - sta) > 30000){
            sta = System.currentTimeMillis();
            Log.d("wave", "showWave");
        }
        if(mLineChart == null) return;
        if(activity != null){
            activity.runOnUiThread(()->{
                LineData data = mLineChart.getData();
                if (data != null) {
                    for (float yValue: _yValues){
                        //Log.d(TAG, "yValue = " + yValue);
                        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                        if (set == null) {
                            set = createSet();
                            data.addDataSet(set);
                        }
                        //如果缓冲区种有数据，就把缓冲区的数据放进data
                        if(entryList.size() > 0){
                            for (int i = 0; i < entryList.size(); i++){
                                Entry entry = entryList.get(i);
                                entry.setX(i);
                                data.addEntry(entry, 0);
                            }
                            entryList.clear();
                        }
                        data.addEntry(new Entry(data.getEntryCount(), yValue), 0);
                    }

                    data.notifyDataChanged();

                    // let the chart know it's data has changed
                    mLineChart.notifyDataSetChanged();

                    // limit the number of visible entries
                    mLineChart.setVisibleXRangeMaximum(maxCount);
                    mLineChart.setVisibleXRangeMinimum(maxCount);

                    // move to the latest entry
                    mLineChart.moveViewToX(data.getEntryCount());
                    //Log.d(TAG, "长度 = " + data.getEntryCount());
                    //如果数据累计过多就清除，把一屏幕的数据放进缓冲区，备用
                    if(data.getEntryCount() > maxCount * 50){
                        entryList.clear();
                        for(int i = data.getEntryCount() - maxCount; i < data.getEntryCount(); i++){
                            entryList.add(data.getDataSetByIndex(0).getEntryForIndex(i));
                        }
                        mLineChart.clearValues();
                    }
                }
            });

        }

    }
    public void addMultiEntry(float[] yValues) {
        if(!isShow) return;
        if((System.currentTimeMillis() - sta) > 30000){
            sta = System.currentTimeMillis();
            Log.d("wave", "showWave");
        }
        if(mLineChart == null) return;

        LineData data = mLineChart.getData();
        if (data != null) {
            yValues = downsampling(yValues);
            if(yValues == null) return;
            if(activity != null){
                float[] finalYValues = yValues;
                activity.runOnUiThread(()->{
                for (float yValue: finalYValues){
                    //Log.d(TAG, "yValue = " + yValue);
                    LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
                    if (set == null) {
                        set = createSet();
                        data.addDataSet(set);
                    }
                    //如果缓冲区种有数据，就把缓冲区的数据放进data
                    if(entryList.size() > 0){
                        for (int i = 0; i < entryList.size(); i++){
                            Entry entry = entryList.get(i);
                            entry.setX(i);
                            data.addEntry(entry, 0);
                        }
                        entryList.clear();
                    }
                    data.addEntry(new Entry(data.getEntryCount(), yValue), 0);
                    data.notifyDataChanged();

                    // let the chart know it's data has changed
                    mLineChart.notifyDataSetChanged();

                    // limit the number of visible entries
                    mLineChart.setVisibleXRangeMaximum(maxCount);
                    mLineChart.setVisibleXRangeMinimum(maxCount);

                    // move to the latest entry
                    mLineChart.moveViewToX(data.getEntryCount());
                    //Log.d(TAG, "长度 = " + data.getEntryCount());
                    //如果数据累计过多就清除，把一屏幕的数据放进缓冲区，备用
                    if(data.getEntryCount() > maxCount * 50){
                        entryList.clear();
                        for(int i = data.getEntryCount() - maxCount; i < data.getEntryCount(); i++){
                            entryList.add(data.getDataSetByIndex(0).getEntryForIndex(i));
                        }
                        mLineChart.clearValues();
                    }
                }

                });
            }

        }


    }


    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "脑电（uV）");
        set.setColor(lineColor);
        //set.setValueTextColor(lineColor);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setLineWidth(1f);
        return set;
    }


    /**
     * 数据降采样处理
     * 输入数据
     * 返回数据
     * */
    boolean isOdd = true;//是否奇数，用于降采样
    float []dataY = {};
    private float [] downsampling(float [] data){
        if(data == null || data.length == 0) return null;
        int dataLeng = data.length/2 + (isOdd?1:0);
        float[] d = new float[dataLeng];
        if(isOdd){
            for (int i = 0; i < d.length; i++) d[i] = data[i*2];
        }else {
            for (int i = 0; i < d.length; i++) d[i] = data[i*2 + 1];
        }
        isOdd = isOdd ? false : true;
        if(dataY.length >= 5) dataY = new float[0];
        dataY = FloatUtil.floatMerger(dataY, d);
        //System.out.println("Y值长度：" + dataY.length);
        if(dataY.length < 5) return null;
        return dataY;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
