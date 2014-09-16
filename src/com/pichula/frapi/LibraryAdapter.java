package com.pichula.frapi;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.pichula.frapi.api.Dictionary;
import com.pichula.frapi.api.DictionaryHandler;
import com.pichula.frapi.api.Frapi;

public class LibraryAdapter extends BaseAdapter implements OnItemClickListener {

	private Activity m_context;
	Vector<DictionaryItem> data;

	public LibraryAdapter(Activity a) {
		m_context = a;
		data = DataController.instance.getDictionaryItems(false);
	}

	public void updateData() {
		data = DataController.instance.getDictionaryItems(false);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int arg0, View v, ViewGroup arg2) {
		if(v==null){
			v=m_context.getLayoutInflater().inflate(R.layout.item_dictionary, null);
		}
		TextView name=(TextView) v.findViewById(R.id.name);
		TextView url=(TextView) v.findViewById(R.id.url);
		Button action=(Button) v.findViewById(R.id.action);
		name.setText(data.get(arg0).name);
		url.setText(data.get(arg0).url);
		switch(data.get(arg0).status){
		case DictionaryItem.STATUS_PARSING:
			action.setVisibility(View.GONE);
			v.findViewById(R.id.progress).setVisibility(View.VISIBLE);
			break;
		case DictionaryItem.STATUS_PARSED:
			v.findViewById(R.id.progress).setVisibility(View.GONE);
			action.setVisibility(View.VISIBLE);
			action.setText("VER");
			action.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i=new Intent(m_context, CloudActivity.class);
					i.putExtra("id", data.get(arg0)._id);
					m_context.startActivity(i);
				}
			});
			v.findViewById(R.id.progress).setVisibility(View.GONE);
			break;
		case DictionaryItem.STATUS_ERROR:
			action.setVisibility(View.VISIBLE);
			v.findViewById(R.id.progress).setVisibility(View.GONE);
			action.setText("Intentar otra vez");
			action.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					cargaDiccionario(arg0);
				}
			});
			
			break;
		case DictionaryItem.STATUS_DOWNLOADING:
			action.setVisibility(View.GONE);
			v.findViewById(R.id.progress).setVisibility(View.VISIBLE);
			break;
		default:
			action.setVisibility(View.VISIBLE);
			v.findViewById(R.id.progress).setVisibility(View.GONE);
			action.setText("ACTIVAR");
			action.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					cargaDiccionario(arg0);
				}
			});
			break;
		}
		
		return v;
	}

	private void cargaDiccionario(final int pos){
		if (data.get(pos).status == DictionaryItem.STATUS_PARSING
				|| data.get(pos).status == DictionaryItem.STATUS_PARSED
				|| data.get(pos).status == DictionaryItem.STATUS_DOWNLOADING)
			return;
		data.get(pos).status = DictionaryItem.STATUS_DOWNLOADING;
		Dictionary.create(data.get(pos).url, 1, data.get(pos)._id,
				new DictionaryHandler() {
					@Override
					public void onSuccess(String s, Dictionary d) {
						super.onSuccess(s, d);
						d.name = data.get(pos).name;
						Frapi.add(d, DataController.instance.getLibrary());
						data.get(pos).status = DictionaryItem.STATUS_PARSED;
						notifyDataSetChanged();
					}

					@Override
					public void onError(String s) {
						super.onError(s);
						data.get(pos).status = DictionaryItem.STATUS_ERROR;
						notifyDataSetChanged();
					}

					@Override
					public void onProgress(String s) {
						super.onProgress(s);
						data.get(pos).status = DictionaryItem.STATUS_PARSING;
						notifyDataSetChanged();
					}

				});
		notifyDataSetChanged();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int pos,
			long arg3) {
		
	}

}
