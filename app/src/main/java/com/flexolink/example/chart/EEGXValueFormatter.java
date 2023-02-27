package com.flexolink.example.chart;

import com.github.mikephil.charting.formatter.ValueFormatter;

/**
 * Description:
 * Created by huwei on 2021/11/25 16:06
 */
public class EEGXValueFormatter extends ValueFormatter{
    int gap = 125;
    public EEGXValueFormatter(int gap) {
        this.gap = gap;
    }

    @Override
    public String getFormattedValue(float value) {
        int position = (int)value;
        String strValue = position/gap+"s";
        return strValue;
    }
}
