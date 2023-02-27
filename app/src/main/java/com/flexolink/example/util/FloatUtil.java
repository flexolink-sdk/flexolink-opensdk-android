package com.flexolink.example.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Description:
 * Created by huwei on 2022/8/24 18:05
 */
public class FloatUtil {

    // 方法一：NumberFormat
    public static String big(double d) {
        NumberFormat nf = NumberFormat.getInstance();
        // 是否以逗号隔开, 默认true以逗号隔开,如[123,456,789.128]
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(8);
        // 结果未做任何处理
        return nf.format(d);
    }

    //方法二： BigDecimal
    public static String big2(double d) {
        BigDecimal d1 = new BigDecimal(Double.toString(d));
        BigDecimal d2 = new BigDecimal(Integer.toString(1));
        // 四舍五入,保留2位小数
        return d1.divide(d2,6,BigDecimal.ROUND_HALF_UP).toString();
    }
    //double数组拼接
    public static float[] floatMerger(float[] bt1, float[] bt2){
        float[] bt3 = new float[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }
}
