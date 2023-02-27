package com.flexolink.example.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.flexolink.example.R;

import java.util.List;

import flexolink.sdk.core.bleDeviceSdk.sdklib.bean.BleBean;

/**
 * 自定义PopupWindow  主要用来显示ListView
 * @author Ansen
 * @param <T>
 * @param <T>
 * @create time 2015-11-3
 */
public class ScanBlePopWindow<T> extends PopupWindow {
	private LayoutInflater inflater;
	private ListView mListView;
	private List<T> list;
	private MyAdapter  mAdapter;

	public ScanBlePopWindow(Context context, List<T> list, OnItemClickListener clickListener) {
		super(context);
		inflater=LayoutInflater.from(context);
		this.list=list;
		init(clickListener);
	}

	public void setList(List<T> list) {
		this.list = list;
	}
	public void setListRefresh(List<T> list) {
		this.list = list;
		if(mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}
	}
	private void init(OnItemClickListener clickListener){
		View view = inflater.inflate(R.layout.spiner_window_layout, null);
		setContentView(view);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		//setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00);
		setBackgroundDrawable(dw);
		setOutsideTouchable(true);
		mListView = (ListView) view.findViewById(R.id.listview);
		mAdapter=new MyAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(clickListener);
	}
	public void setTouchListener(View.OnTouchListener listener){
		if(mListView != null){
			mListView.setOnTouchListener(listener);
		}
	}
	private class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView==null){
				holder=new ViewHolder();
				convertView=inflater.inflate(R.layout.spiner_item_layout, null);
				holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			BleBean updateSleepDataFinishedBean = (BleBean) getItem(position);
			if(updateSleepDataFinishedBean != null){
				holder.tv_name.setText(updateSleepDataFinishedBean.getName());
			}
			return convertView;
		}
	}

	private class ViewHolder{
		private TextView tv_name;
	}
}
