package com.pichula.frapi;

import java.io.InputStream;
import java.util.Vector;

import android.content.res.AssetManager;

import com.pichula.frapi.api.Dictionary;
import com.pichula.frapi.api.DictionaryHandler;
import com.pichula.frapi.api.Library;

public class DataController {

	public static DataController instance = null;

	private Vector<DictionaryItem> urlItems;
	private Dictionary spanish = null;

	private Library library;

	static {
		instance = new DataController();
	}

	public void loadSpanishDictionary(DictionaryHandler h) {
		if (spanish == null) {
			try {
				AssetManager am = MainApplication.getContext().getAssets();

				InputStream is = am.open("es.txt");
				spanish = Dictionary.create(is, 1, h);
			} catch (Exception e) {
				e.printStackTrace();
				h.onError("Error!");
			}
		} else {
			h.onSuccess("Cargado!", spanish);
		}
	}

	public Dictionary getSpanishDictionary() {
		return spanish;
	}

	private DataController() {
		urlItems = new Vector<DictionaryItem>();
		urlItems = CupboardSQLiteOpenHelper.getDictionaryItems();
		library = new Library();
	}

	public Vector<DictionaryItem> getDictionaryItems(boolean update) {
		if (update) {
			urlItems = new Vector<DictionaryItem>();
			urlItems = CupboardSQLiteOpenHelper.getDictionaryItems();
		}
		return urlItems;
	}

	public Library getLibrary() {
		return library;
	}

	public void addDictionaryItem(DictionaryItem di) {
		di._id = CupboardSQLiteOpenHelper.addDictionaryItem(di);
		urlItems.add(di);
	}

	public Dictionary getDictionary(long dict_id) {
		for (Dictionary d : library.dics) {
			if (d.id == dict_id)
				return d;
		}
		return null;
	}

}
