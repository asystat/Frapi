package com.pichula.frapi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.pichula.frapi.api.Dictionary;
import com.pichula.frapi.api.DictionaryHandler;
import com.pichula.frapi.api.Frapi;
import com.pichula.frapi.api.MutableFloat;

public class MainActivity extends Activity {

	DictionaryHandler createHandler1, createHandler2;
	Dictionary dic1,dic2;
	TextView log;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		log=(TextView) findViewById(R.id.log);
		
		createHandler1=new DictionaryHandler(){
			@Override
			public void onSuccess(String s, Dictionary d) {
				super.onSuccess(s,d);
				log.append(s+"\n");		
				if(dic2.ready)
					showDictionaries();
			}
		};
		
		createHandler2=new DictionaryHandler(){
			@Override
			public void onSuccess(String s, Dictionary d) {
				super.onSuccess(s, d);
				log.append(s+"\n");	
				if(dic1.ready)
					showDictionaries();
			}
		};
		
		dic1=Frapi.create("http://aches.es/xp/XtremeAndroid/dic1.txt", 1,1,createHandler1);
		dic2=Frapi.create("http://aches.es/xp/XtremeAndroid/dic2.txt", 1,2,createHandler2);
		
		
		//Frapi.create(url, n, h)
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	private void showDictionaries(){
		
		dic2=dic1.commons(dic2);
		
		Map<String, MutableFloat>  mp1=dic1.getWords();
		Map<String, MutableFloat>  mp2=dic2.getWords();
		
		log.append("Dic1:\n");
		Iterator it = mp1.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			log.append(pairs.getKey()+" - "+(((MutableFloat)pairs.getValue()).get())+"\n");
		}
		
		log.append("\n\nDic2:\n");
		it = mp2.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			log.append(pairs.getKey()+" - "+(((MutableFloat)pairs.getValue()).get())+"\n");
		}
	}

}
