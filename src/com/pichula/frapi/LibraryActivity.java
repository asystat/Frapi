package com.pichula.frapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pichula.frapi.api.Dictionary;
import com.pichula.frapi.api.DictionaryHandler;
import com.pichula.frapi.api.Frapi;
import com.pichula.frapi.api.Library;

public class LibraryActivity extends Activity implements OnClickListener {

	private ListView list;
	private Button addUrl, analize;
	private LibraryAdapter adapter;
	private View loadingLayout;
	private TextView loadingMessage;

	protected Dialog mSplashDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MyStateSaver data = (MyStateSaver) getLastNonConfigurationInstance();
	    if (data != null) {
	        // Show splash screen if still loading
	        if (data.showSplashScreen) {
	            showSplashScreen();
	        }
	        setContentView(R.layout.activity_list);
	 
	        // Rebuild your UI with your saved state here
	    } else {
	        showSplashScreen();
	        setContentView(R.layout.activity_list);
	        // Do your heavy loading here on a background thread
	    }

	    
	    if (KeyStoreController.getKeyStore().isFirstLaunch()){
			CupboardSQLiteOpenHelper.addDefaults();
			DataController.instance.getDictionaryItems(true);
	    }
		
		loadingLayout=findViewById(R.id.loading_layout);
		loadingMessage=(TextView)findViewById(R.id.loading_message);
		
		list = (ListView) findViewById(R.id.list);
		adapter = new LibraryAdapter(this);
		list.setAdapter(adapter);

		list.setOnItemClickListener(adapter);

		addUrl = (Button) findViewById(R.id.addurl);
		addUrl.setOnClickListener(this);

		analize = (Button) findViewById(R.id.analize);
		analize.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.addurl:

			final Dialog d = new Dialog(this);
			d.setTitle("A–adir URL");
			d.setContentView(R.layout.dialog_add_url);
			final EditText nombre = (EditText) d.findViewById(R.id.nombre);
			final EditText url = (EditText) d.findViewById(R.id.url);
			Button insertar = (Button) d.findViewById(R.id.add);
			insertar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DictionaryItem di = new DictionaryItem();
					di.imgurl = "";
					di.url = url.getText().toString().toLowerCase();
					di.name = nombre.getText().toString();

					if(di.name.length()==0 || di.url.length()<4)
						return;
					
					if (!di.url.matches("^\\w+://.*")) {
						di.url = "http://" + di.url;
					}
					
					di._id = null;
					DataController.instance.addDictionaryItem(di);
					adapter.updateData();
					adapter.notifyDataSetChanged();
					d.dismiss();
				}
			});
			d.show();

			break;
		case R.id.analize:
			if (DataController.instance.getLibrary().dics.size() < 2) {
				new AlertDialog.Builder(this)
						.setTitle("Atenci—n").setMessage("Para poder hacer an‡lisis inteligente, tienes que activar al menos 2 fuentes!")
						.setCancelable(true)
						.create().show();
			}
			else{
				//cargamos diccionario en espa–ol
				loadingLayout.setVisibility(View.VISIBLE);
				DataController.instance.loadSpanishDictionary(new DictionaryHandler(){
					
					@Override
					public void onSuccess(String s, Dictionary d) {
						pasaDiccionarioYOrdena();
					}
					
					@Override
					public void onProgress(String s) {
						loadingMessage.setText(s);
						super.onProgress(s);
					}
					
					@Override
					public void onError(String s) {
						loadingLayout.setVisibility(View.GONE);
						super.onError(s);
					}
					
				});
				
			}
			
			
			break;
		}
	}
	
	private void pasaDiccionarioYOrdena(){
		if(smartTask!=null)
			smartTask.cancel(true);
		smartTask=new PDYO();
		smartTask.execute();
	}
	PDYO smartTask=null;
	
	private class PDYO extends AsyncTask<Void, String, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			Library l=DataController.instance.getLibrary();
			Dictionary spa=DataController.instance.getSpanishDictionary();
			int index=1;
			for(Dictionary d: l.dics){
				publishProgress("Filtrando entradas del diccionario "+index);
				d.substract_save(spa);
				publishProgress("Ordenando diccionario "+index);
				d.sort(Dictionary.P_APPEARANCE_DESC);
				publishProgress("Calculando distancias "+index);
				Frapi.distances(l, d.id);
				index++;
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			loadingMessage.setText(values[0]);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			loadingLayout.setVisibility(View.GONE);
			Intent i=new Intent(LibraryActivity.this, SmartActivity.class);
			startActivity(i);
		}
		
	}
	
	
	
	
	//***splashscreeen***//
	
	
	
	@Override
	public Object onRetainNonConfigurationInstance() {
	    MyStateSaver data = new MyStateSaver();
	    // Save your important data here
	 
	    if (mSplashDialog != null) {
	        data.showSplashScreen = true;
	        removeSplashScreen();
	    }
	    return data;
	}
	 
	/**
	 * Removes the Dialog that displays the splash screen
	 */
	protected void removeSplashScreen() {
	    if (mSplashDialog != null) {
	        mSplashDialog.dismiss();
	        mSplashDialog = null;
	    }
	}
	 
	/**
	 * Shows the splash screen over the full Activity
	 */
	protected void showSplashScreen() {
	    mSplashDialog = new Dialog(this, R.style.SplashScreen);
	    mSplashDialog.setContentView(R.layout.splash_screen);
	    mSplashDialog.setCancelable(false);
	    mSplashDialog.show();
	    final TextView splashStatus=(TextView) mSplashDialog.findViewById(R.id.splashstatus);
	     
	    // Set Runnable to remove splash screen just in case
	    DataController.instance.loadSpanishDictionary(new DictionaryHandler(){
	    	@Override
	    	public void onSuccess(String s, Dictionary d) {
	    		super.onSuccess(s, d);
	    		removeSplashScreen();
	    	}
	    	
	    	int p=0;
	    	@Override
	    	public void onProgress(String s) {
	    		p++;
	    		if(p>=2)
	    			splashStatus.setText("Ya casi he terminado de cargar todo!");
	    		super.onProgress(s);
	    	}
	    	
	    	@Override
	    	public void onError(String s) {
	    		super.onError(s);
	    		removeSplashScreen();
	    	}
	    });
	}
	 
	/**
	 * Simple class for storing important data across config changes
	 */
	private class MyStateSaver {
	    public boolean showSplashScreen = false;
	    // Your other important fields here
	}
	
	

}
