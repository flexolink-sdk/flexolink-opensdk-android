package com.flexolink.example;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Description:
 * Created by huwei on 2022/10/12 15:13
 */
public class FileUtil {
    //文件读取
    public static float [] readFileData(Context context, String filePath){
        try {
            InputStream inputStream = context.getAssets().open(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            ArrayList<Float> dataList = new ArrayList<>();
            while((line = bufferedReader.readLine()) != null){
                //Log.d("huwei", line);
                float value = Float.parseFloat(line);
                dataList.add(value);
            }
            float [] result = new float[dataList.size()];
            for(int i = 0; i < result.length; i++){
                result[i] = dataList.get(i);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //文件读取
    public static double [] readFileDoubleData(Context context, String filePath){
        try {
            InputStream inputStream = context.getAssets().open(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            ArrayList<Float> dataList = new ArrayList<>();
            while((line = bufferedReader.readLine()) != null){
                //Log.d("huwei", line);
                float value = Float.parseFloat(line);
                dataList.add(value);
            }
            double [] result = new double[dataList.size()];
            for(int i = 0; i < result.length; i++){
                result[i] = dataList.get(i);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /***
     * 按行写文件
     * @param line  写的内容
     * @param path  文件路径
     * @param append
     * @return 拷贝成功 返回true, 失败返回false
     */
    public static int writeFileByLine(String line, String path, boolean append) {
        BufferedWriter bw = null;
        FileWriter fileWriter = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getAbsoluteFile(), append);
            bw = new BufferedWriter(fileWriter);
            bw.write(line);
            if (append == true)
                bw.newLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bw.flush();
                bw.close();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

}
