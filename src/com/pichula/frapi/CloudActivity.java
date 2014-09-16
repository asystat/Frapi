package com.pichula.frapi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.komodo.tagin.SampleTagCloud;
import com.pichula.frapi.SatelliteMenu.SateliteClickedListener;
import com.pichula.frapi.api.Dictionary;
import com.pichula.frapi.api.DictionaryHandler;
import com.pichula.frapi.api.Frapi;
import com.pichula.frapi.api.MutableFloat;

public class CloudActivity extends Activity implements SateliteClickedListener {

	private String[] TAGS = null;

	Dictionary dic;
	ListView lista;
	CloudAdapter adapter;
	View loadingLayout;
	TextView loadingMesage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud);

		lista = (ListView) findViewById(R.id.cloud_view);

		loadingLayout = findViewById(R.id.loading_layout);
		loadingMesage = (TextView) findViewById(R.id.loading_message);
		loadingLayout.setVisibility(View.GONE);

		long dict_id = getIntent().getLongExtra("id", 0);
		dic = DataController.instance.getDictionary(dict_id);
		if (dic == null)
			finish();
		
		setTitle(dic.name);

		adapter = new CloudAdapter(this, dic);
		lista.setAdapter(adapter);

		populateTags();

		SatelliteMenu menu = (SatelliteMenu) findViewById(R.id.smenu);
		List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
		items.add(new SatelliteMenuItem(6, R.drawable.ic_limpiar));
		items.add(new SatelliteMenuItem(5, R.drawable.ic_ordenar));
		items.add(new SatelliteMenuItem(4, R.drawable.ic_distancias));
		items.add(new SatelliteMenuItem(3, R.drawable.ic_entropia));
		items.add(new SatelliteMenuItem(2, R.drawable.ic_restar));
		items.add(new SatelliteMenuItem(1, R.drawable.ic_sumar));
		menu.addItems(items);
		menu.setOnItemClickedListener(this);

	}

	private void populateTags() {
		if (TAGS == null) {
			TAGS = new String[(int) dic.Fi];
			Iterator it = dic.getWords().entrySet().iterator();
			int counter = 0;
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				int repetitions = (int) ((MutableFloat) pairs.getValue()).get();
				for (int i = 0; i < repetitions; i++)
					TAGS[counter++] = (String) pairs.getKey();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void eventOccured(int id) {
		switch (id) {
		case 6:
			loadingLayout.setVisibility(View.VISIBLE);
			DataController.instance
					.loadSpanishDictionary(new DictionaryHandler() {

						@Override
						public void onSuccess(String s, Dictionary d) {
							loadingLayout.setVisibility(View.GONE);
							dic = dic.substract(d);
							adapter.setDictionary(dic);
							adapter.notifyDataSetChanged();
							// DataController.instance.getLibrary();
						}

						@Override
						public void onProgress(String s) {
							loadingMesage.setText(s);
							super.onProgress(s);
						}

						@Override
						public void onError(String s) {
							loadingLayout.setVisibility(View.GONE);
							super.onError(s);
						}

					});

			break;

		case 5:
			final Dialog d = new Dialog(this);
			d.setContentView(R.layout.dialog_orden);
			d.setTitle("Orden");
			d.findViewById(R.id.alfa).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dic.sort(Dictionary.P_ALPHABETICALLY);
					adapter.setDictionary(dic);
					adapter.notifyDataSetChanged();
					d.dismiss();
				}
			});
			d.findViewById(R.id.alfa_desc).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							dic.sort(Dictionary.P_ALPHABETICALLY_DESC);
							adapter.setDictionary(dic);
							adapter.notifyDataSetChanged();
							d.dismiss();
						}
					});
			d.findViewById(R.id.freq).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dic.sort(Dictionary.P_APPEARANCE);
					adapter.setDictionary(dic);
					adapter.notifyDataSetChanged();
					d.dismiss();
				}
			});
			d.findViewById(R.id.freq_desc).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							dic.sort(Dictionary.P_APPEARANCE_DESC);
							adapter.setDictionary(dic);
							adapter.notifyDataSetChanged();
							d.dismiss();
						}
					});
			d.show();
			break;

		case 4:
			Frapi.distances(DataController.instance.getLibrary(), dic.id);
			Toast.makeText(
					this,
					"Las distancias con otras fuentes han sido calculadas. Para ver las diferentes distancias, anda a la funcion \"Smart Scan\" de la pantalla anterior!",
					Toast.LENGTH_LONG).show();
			break;
		case 3:/*
			float entr = Frapi.entropy(dic);
			Toast.makeText(this,
					"La entrop’a de las palabras en esta fuente es de " + entr,
					Toast.LENGTH_SHORT).show();*/
			Intent i=new Intent(this, SampleTagCloud.class);
			i.putExtra("id", dic.id);
			startActivity(i);
			break;

		case 2:
			addSelectSource(false);
			break;
		case 1:
			addSelectSource(true);
			break;
		}

	}

	private void addSelectSource(final boolean sum) {
		int nDictionaries = DataController.instance.getLibrary().dics.size();
		final String items[] = new String[nDictionaries];
		final long item_ids[] = new long[nDictionaries];

		int index = 0;
		for (Dictionary d : DataController.instance.getLibrary().dics) {
			items[index] = d.name;
			item_ids[index] = d.id;
			index++;
		}

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("Seleccione una fuente");
		ab.setItems(items, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface d, int choice) {
				if(sum)
					sum(item_ids[choice]);
				else
					sub(item_ids[choice]);
			}
		});
		ab.show();
	}

	private void sum(final long did) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("ÀSeguro que quieres modificar el diccionario?")
				.setCancelable(false)
				.setPositiveButton("Si",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Dictionary otherDic=null;
								for (Dictionary d : DataController.instance
										.getLibrary().dics) {
									if(did == d.id){
										otherDic=d;
										break;
									}
								}
								if(otherDic!=null){
									dic.sum_save(otherDic);
									adapter.updateData(dic);
								}
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void sub(final long did) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("ÀSeguro que quieres modificar el diccionario?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Dictionary otherDic=null;
								for (Dictionary d : DataController.instance
										.getLibrary().dics) {
									if(did == d.id){
										otherDic=d;
										break;
									}
								}
								if(otherDic!=null){
									dic.substract_save(otherDic);
									adapter.updateData(dic);
								}
								
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
