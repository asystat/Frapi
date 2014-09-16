package com.pichula.frapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class SmartActivity extends Activity{
	
	ListView list;
	SmartAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smart_layout);
		list=(ListView)findViewById(R.id.list);
		adapter=new SmartAdapter(this);
		list.setAdapter(adapter);
		
		
	}

}
