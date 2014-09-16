package com.pichula.frapi;

import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pichula.frapi.api.Dictionary;
import com.pichula.frapi.api.MutableFloat;

public class CloudAdapter extends BaseAdapter{

	private Activity context;
	private Dictionary dic;
	private Vector mData;
	
	public CloudAdapter(Activity a, Dictionary d){
		dic=d;
		mData=new Vector();
		mData.addAll(d.getWords().entrySet());
		context=a;
	}
	
	public void updateData(Dictionary d){
		dic=d;
		mData=new Vector();
		mData.addAll(d.getWords().entrySet());
		notifyDataSetChanged();
	}

	
	public void setDictionary(Dictionary d){
		dic=d;
		mData=new Vector();
		mData.addAll(d.getWords().entrySet());
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Map.Entry<String, MutableFloat> getItem(int position) {
        return (Map.Entry) mData.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View v, ViewGroup arg2) {
		if(v==null){
			v=context.getLayoutInflater().inflate(R.layout.item_cloudlist, null);
		}
		Map.Entry<String, MutableFloat> item = getItem(arg0);
		CustomView c=(CustomView) v.findViewById(R.id.custom);
		c.setPercent((item.getValue().get()/dic.maxCount)*100);
		
		TextView word=(TextView)v.findViewById(R.id.word);
		word.setText(item.getKey()+"    x"+((int)item.getValue().get()));
		
		return v;
	}

}
